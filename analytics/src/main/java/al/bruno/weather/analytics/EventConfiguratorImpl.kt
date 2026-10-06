package al.bruno.weather.analytics

/** How one property is declared, per service; the `null` key applies to every service. */
internal sealed interface PropertySpec {
    data object Required : PropertySpec
    data object Optional : PropertySpec
    data class OfValue(val value: String) : PropertySpec
}

/**
 * Collects an [Event]'s DSL declarations, then resolves name and properties for each service.
 *
 * Resolution order for `require`/`optional`: fixed properties, then providers (each provider is
 * asked at most once per event).
 */
internal class EventConfiguratorImpl(
    private val eventName: String,
    private val fixedProperties: Map<String, String>,
    private val provide: (propertyName: String) -> Map<String, String>,
) : EventConfigurator {

    private val declarations = mutableMapOf<String, MutableMap<ServiceId?, PropertySpec>>()
    private val nameTransformations = mutableMapOf<ServiceId, (String) -> String>()
    private val providedValues = mutableMapOf<String, String>()
    private val askedProviders = mutableSetOf<String>()

    override fun service(serviceId: ServiceId, configuration: ServiceSpecificEventConfigurator.() -> Unit) {
        ServiceConfigurator(serviceId).configuration()
    }

    override fun set(name: String, value: String) = declare(name, null, PropertySpec.OfValue(value))
    override fun require(name: String) = declare(name, null, PropertySpec.Required)
    override fun optional(name: String) = declare(name, null, PropertySpec.Optional)

    private fun declare(name: String, serviceId: ServiceId?, spec: PropertySpec) {
        kotlin.require(name.isNotBlank()) { "Property names must not be blank (event '$eventName')" }
        declarations.getOrPut(name) { mutableMapOf() }[serviceId] = spec
    }

    fun buildName(serviceId: ServiceId): String = nameTransformations[serviceId]?.invoke(eventName) ?: eventName

    /** @throws IllegalStateException when a required property has no value */
    fun buildProperties(serviceId: ServiceId): Map<String, String> = buildMap {
        for ((name, specs) in declarations) {
            when (val spec = specs[serviceId] ?: specs[null]) {
                null -> Unit                                         // declared for other services only
                is PropertySpec.OfValue -> put(name, spec.value)
                PropertySpec.Optional -> resolve(name)?.let { put(name, it) }
                PropertySpec.Required -> put(
                    name,
                    resolve(name) ?: error("No value for required property '$name' (event '$eventName')"),
                )
            }
        }
    }

    private fun resolve(name: String): String? =
        fixedProperties[name] ?: providedValues[name] ?: run {
            if (askedProviders.add(name)) providedValues.putAll(provide(name))
            providedValues[name]
        }

    private inner class ServiceConfigurator(private val serviceId: ServiceId) : ServiceSpecificEventConfigurator {
        override fun name(transformer: (String) -> String) {
            this@EventConfiguratorImpl.nameTransformations[serviceId] = transformer
        }

        override fun set(name: String, value: String) = this@EventConfiguratorImpl.declare(name, serviceId, PropertySpec.OfValue(value))
        override fun require(name: String) = this@EventConfiguratorImpl.declare(name, serviceId, PropertySpec.Required)
        override fun optional(name: String) = this@EventConfiguratorImpl.declare(name, serviceId, PropertySpec.Optional)
    }
}
