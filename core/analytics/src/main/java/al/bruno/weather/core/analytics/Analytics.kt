package al.bruno.weather.core.analytics

import al.bruno.weather.core.logging.Logging
import java.util.concurrent.ConcurrentHashMap

/** Entry point for sending analytics. Create one with [analytics]. */
interface Analytics {
    /** Initializes every service. Call once, at app start. */
    fun initialize()

    /** Resolves [event] for each matching service and sends it. Never throws. */
    fun logEvent(event: Event)

    /** Values for `require`/`optional` properties that never change (app version, build type...). Existing keys are replaced. */
    fun setProperties(properties: Map<String, String>)

    /** @param serviceId null means every service */
    fun setUserProperty(name: String, value: String, serviceId: ServiceId? = null)

    /** @param serviceId null means every service */
    fun setUserId(id: String, serviceId: ServiceId? = null)
}

/** `analytics.logEvent("retry_tapped") { set("screen", "weather") }` */
fun Analytics.logEvent(
    name: String,
    serviceId: ServiceId? = null,
    configuration: EventConfiguration = {},
) = logEvent(event(name, serviceId, configuration))

fun Analytics.logEvents(vararg events: Event) = events.forEach(::logEvent)

/**
 * Builds an [Analytics] instance.
 *
 * ```
 * val analytics = analytics {
 *     logging = appLogging
 *     service(LoggingAnalyticsService(appLogging))
 *     properties("app_version") { mapOf("app_version" to BuildConfig.VERSION_NAME) }
 *     logEvents()
 *     history(InMemoryAnalyticsHistory())
 *     redaction { !BuildConfig.DEBUG }
 * }
 * ```
 */
fun analytics(block: AnalyticsBuilder.() -> Unit): Analytics = AnalyticsBuilder().apply(block).build()

@AnalyticsDsl
class AnalyticsBuilder internal constructor() {
    /** Used for the library's own error messages and for [logEvents]. */
    var logging: Logging = Logging.NONE

    private val services = mutableListOf<AnalyticsService>()
    private val providers = mutableListOf<PropertiesProvider>()
    private val interceptors = mutableListOf<() -> EventInterceptor>()
    private var redactionEnabled: () -> Boolean = { true }

    fun service(service: AnalyticsService) {
        services += service
    }

    fun properties(provider: PropertiesProvider) {
        providers += provider
    }

    fun properties(vararg names: String, values: () -> Map<String, String>) {
        providers += propertiesProvider(*names, values = values)
    }

    fun intercept(interceptor: EventInterceptor) {
        interceptors += { interceptor }
    }

    /** Logs every sent event at DEBUG through [logging]. */
    fun logEvents() {
        interceptors += { LoggingInterceptor(logging) }
    }

    /** Records every event (sent or failed) into [history]. */
    fun history(history: AnalyticsHistory) {
        intercept(HistoryInterceptor(history))
    }

    /** Whether services should strip personal data. Evaluated on every event; defaults to always. */
    fun redaction(enabled: () -> Boolean) {
        redactionEnabled = enabled
    }

    internal fun build(): Analytics {
        val ids = services.map { it.id }
        require(ids.size == ids.toSet().size) { "Two services share an id: $ids" }
        return DefaultAnalytics(
            services = services.toList(),
            providers = providers.toList(),
            interceptors = interceptors.map { it() },
            redactionEnabled = redactionEnabled,
            logging = logging,
        )
    }
}

internal class DefaultAnalytics(
    private val services: List<AnalyticsService>,
    providers: List<PropertiesProvider>,
    private val interceptors: List<EventInterceptor>,
    private val redactionEnabled: () -> Boolean,
    logging: Logging,
) : Analytics {

    private val log = logging.logger("Analytics")
    private val fixedProperties = ConcurrentHashMap<String, String>()
    private val providerByProperty: Map<String, PropertiesProvider> = buildMap {
        for (provider in providers) {
            require(provider.providedProperties.isNotEmpty()) { "A properties provider must declare at least one property" }
            for (name in provider.providedProperties) {
                require(name.isNotBlank()) { "Provided property names must not be blank: ${provider.providedProperties}" }
                require(name !in this) { "More than one provider declares '$name'" }
                put(name, provider)
            }
        }
    }

    override fun initialize() = services.forEach { it.initialize() }

    override fun setProperties(properties: Map<String, String>) = fixedProperties.putAll(properties)

    override fun setUserProperty(name: String, value: String, serviceId: ServiceId?) =
        services.filter { serviceId == null || it.id == serviceId }.forEach { it.setUserProperty(name, value) }

    override fun setUserId(id: String, serviceId: ServiceId?) =
        services.filter { serviceId == null || it.id == serviceId }.forEach { it.setUserId(id) }

    override fun logEvent(event: Event) {
        val configurator = EventConfiguratorImpl(event.name, fixedProperties.toMap(), ::provide)
        try {
            event.configuration(configurator)
        } catch (e: Exception) {
            log.e(e) { "Event '${event.name}' has an invalid configuration" }
            return
        }

        val redact = redactionEnabled()
        for (service in services) {
            if (event.serviceId != null && event.serviceId != service.id) continue

            val name = configurator.buildName(service.id)
            var properties = emptyMap<String, String>()
            val error = try {
                properties = configurator.buildProperties(service.id)
                service.logEvent(name, properties, redact)
                null
            } catch (e: Exception) {
                log.e(e) { "Event '$name' was not sent to ${service.id.name}" }
                e.message ?: e::class.java.simpleName
            }
            val processed = ProcessedEvent(service.id, name, properties, error)
            interceptors.forEach { interceptor ->
                try {
                    interceptor.process(processed)
                } catch (e: Exception) {
                    log.e(e) { "Interceptor failed for event '$name'" }
                }
            }
        }
    }

    /** Asks the provider that declares [propertyName]; drops keys it didn't declare. */
    private fun provide(propertyName: String): Map<String, String> {
        val provider = providerByProperty[propertyName] ?: return emptyMap()
        val values = provider.provide()
        val undeclared = values.keys - provider.providedProperties
        if (undeclared.isNotEmpty()) {
            log.w { "Provider for ${provider.providedProperties} returned undeclared properties $undeclared; ignoring them" }
        }
        return values.filterKeys { it in provider.providedProperties }
    }
}
