package al.bruno.weather.analytics

/** Identifies one analytics backend, e.g. `ServiceId("Firebase")`. */
@JvmInline
value class ServiceId(val name: String)

@DslMarker
annotation class AnalyticsDsl

typealias EventConfiguration = EventConfigurator.() -> Unit

/**
 * An analytics event. Implement it for reusable events, or create one inline with [event].
 *
 * ```
 * data class CitySearched(val city: String) : Event {
 *     override val name = "city_searched"
 *     override val configuration: EventConfiguration = {
 *         set("city", city)
 *         require("app_version")              // filled in by a properties provider
 *         service(FIREBASE) { name { "weather_$it" } }
 *     }
 * }
 * ```
 */
interface Event {
    /** The name sent to analytics services (each service may rename it, see [EventConfigurator.service]). */
    val name: String

    /** Only send to this service; null sends to every service. */
    val serviceId: ServiceId?
        get() = null

    /** Declares the event's properties with the DSL. */
    val configuration: EventConfiguration
        get() = {}
}

/** Creates an event inline: `event("retry_tapped") { set("screen", "weather") }`. */
fun event(
    name: String,
    serviceId: ServiceId? = null,
    configuration: EventConfiguration = {},
): Event = InlineEvent(name, serviceId, configuration)

private class InlineEvent(
    override val name: String,
    override val serviceId: ServiceId?,
    override val configuration: EventConfiguration,
) : Event {
    override fun toString() = "Event($name)"
}

@AnalyticsDsl
interface PropertyConfigurator {
    /** A property with a fixed value. */
    fun set(name: String, value: String)

    fun set(name: String, value: Number) = set(name, value.toString())
    fun set(name: String, value: Boolean) = set(name, value.toString())

    /** A property whose value must come from a fixed property or a provider; the event fails without it. */
    fun require(name: String)

    /** A property sent only if a fixed property or a provider has a value for it. */
    fun optional(name: String)
}

@AnalyticsDsl
interface EventConfigurator : PropertyConfigurator {
    /** Overrides for one service: extra properties, or a different event name. */
    fun service(serviceId: ServiceId, configuration: ServiceSpecificEventConfigurator.() -> Unit)
}

@AnalyticsDsl
interface ServiceSpecificEventConfigurator : PropertyConfigurator {
    /** Renames the event for this service only. */
    fun name(transformer: (String) -> String)
}
