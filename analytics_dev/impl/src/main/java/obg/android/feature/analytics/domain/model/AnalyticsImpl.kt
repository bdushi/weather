package obg.android.feature.analytics.domain.model

import obg.android.feature.analytics.domain.model.event.Event
import obg.android.feature.analytics.domain.model.event.EventConfiguratorImpl
import obg.android.feature.analytics.domain.model.property.PropertiesProvider
import timber.log.Timber
import javax.inject.Inject

class AnalyticsImpl @Inject constructor(
    private val services: Set<AnalyticsService>,
    propertyProviders: Set<PropertiesProvider>,
    private val eventInterceptors: List<EventsInterceptor> = emptyList()
) : Analytics {

    private val fixedProperties: MutableMap<String, String> = mutableMapOf()
    private val propertyNameToProvider: Map<String, PropertiesProvider>
    private var isRedactionEnabled: () -> Boolean = { true }

    init {
        validateProviders(propertyProviders)
        propertyNameToProvider = propertyProviders
            .flatMap { provider ->
                provider.providedProperties.map { it to provider }
            }
            .associate { it }
    }

    private fun validateProviders(providers: Collection<PropertiesProvider>) {
        val declaredProperties = mutableSetOf<String>()

        for (provider in providers) {
            require(provider.providedProperties.isNotEmpty()) {
                "All providers must declare at least one property"
            }

            require(provider.providedProperties.none { it.isEmpty() }) {
                val declared = provider.providedProperties.joinToString { "'$it'" }
                "All declared property names in a provider must not be empty (declared: $declared)"
            }

            val reDeclared = provider.providedProperties.find { declaredProperties.contains(it) }
            require(reDeclared == null) {
                "More than one provider declares providing '$reDeclared'"
            }

            declaredProperties.addAll(provider.providedProperties)
        }
    }

    override fun initialize() {
        for (service in services) {
            service.initialize()
        }
    }

    override fun setProperties(properties: Map<String, String>) {
        fixedProperties.putAll(properties)
    }

    override fun setUserProperty(name: String, value: String, serviceId: ServiceId?) {
        services
            .filter { service ->
                serviceId?.let { it == service.id } ?: true
            }
            .forEach { it.setUserProperty(name, value) }
    }

    override fun setUserId(id: String, serviceId: ServiceId?) {
        services
            .filter { service ->
                serviceId?.let { it == service.id } ?: true
            }
            .forEach { it.setUserId(id) }
    }

    override fun setRedactionEnabler(redactionFlagProvider: () -> Boolean) {
        isRedactionEnabled = redactionFlagProvider
    }

    override fun logEvent(event: Event) {
        val eventConfigurator = EventConfiguratorImpl(event.name, fixedProperties) { propertyName ->
            propertyNameToProvider[propertyName]?.provide?.invoke() ?: emptyMap()
        }
        event.configuration(eventConfigurator)

        for (service in services) {
            if (event.isForService(service)) {
                val eventName = eventConfigurator.buildName(service.id)
                var error: String? = null

                val props = try {
                    eventConfigurator.buildProps(service.id)
                } catch (e: IllegalStateException) {
                    Timber.e(
                        e,
                        "Failed to build properties for %s event in %s analytics service",
                        eventName,
                        service.id.name
                    )
                    error = "Failed to build properties\n${e.stackTrace}"
                    null
                } catch (e: IllegalArgumentException) {
                    Timber.e(
                        e,
                        "Event %s is not valid for sending through %s analytics service",
                        eventName,
                        service.id.name
                    )
                    error = "Event is not valid for sending through ${service.id.name} analytics service" +
                        "\n${e.stackTrace}"
                    null
                } ?: emptyMap()

                processAllInterceptors(eventName, props, service.id, error)

                if (error == null) {
                    service.logEvent(eventName, props, isRedactionEnabled())
                }
            }
        }
    }

    private fun processAllInterceptors(
        eventName: String,
        eventProperties: Map<String, String>,
        serviceId: ServiceId,
        error: String?
    ) {
        for (interceptor in eventInterceptors) {
            interceptor.process(eventName, eventProperties, serviceId, error)
        }
    }

    private fun Event.isForService(service: AnalyticsService): Boolean =
        this.serviceId?.let { it == service.id } ?: true

    companion object {
        /**
         * Convenience method that creates simple implementation of PropertyProvider.
         *
         * The [properties] should not generate properties that were not declared
         * in [propertyNames]. All such properties will be logged as error and skipped.
         *
         * @param propertyNames list of names of properties this provider can create
         * @param properties generates properties as map of name to value
         */
        fun propertyProvider(
            vararg propertyNames: String,
            properties: () -> Map<String, String>
        ): PropertiesProvider =
            object : PropertiesProvider {
                override val providedProperties: Set<String> = propertyNames.toSet()

                override val provide: () -> Map<String, String> = {
                    val props = properties()
                    if (props.size > providedProperties.size) {
                        val invalid = props.keys.first { !providedProperties.contains(it) }
                        Timber.e(
                            "Provider generated property '%s' but declares only %s",
                            invalid,
                            providedProperties
                        )
                    }
                    props.filterKeys { propertyNames.contains(it) }
                }
            }
    }
}
