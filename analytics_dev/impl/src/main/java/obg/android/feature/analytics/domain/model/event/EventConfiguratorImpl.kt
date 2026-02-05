package obg.android.feature.analytics.domain.model.event

import obg.android.feature.analytics.domain.model.ServiceId

class EventConfiguratorImpl(
    private val eventName: String,
    private val fixedProperties: Map<String, String> = emptyMap(),
    private val propertyProvider: (String) -> Map<String, String>
) : EventConfigurator {
    private val properties = mutableMapOf<String, PropertyDeclaration>()
    private val nameTransformations = mutableMapOf<ServiceId, (String) -> String>()
    private val providedValues = mutableMapOf<String, String>()

    override fun service(
        serviceId: ServiceId,
        eventConfigurator: ServiceSpecificEventConfigurator.() -> Unit
    ) {
        val subConfigurator = ServiceSpecificConfiguratorImpl(serviceId)
        eventConfigurator.invoke(subConfigurator)
    }

    override fun set(name: String, value: String) {
        addPropertyDeclaration(name, PropertyDeclaration.ofValue(value))
    }

    override fun require(name: String) {
        addPropertyDeclaration(name, PropertyDeclaration.required())
    }

    override fun optional(name: String) {
        addPropertyDeclaration(name, PropertyDeclaration.optional())
    }

    private fun addPropertyDeclaration(name: String, declaration: PropertyDeclaration) {
        val previous = properties[name] ?: PropertyDeclaration.empty()
        properties[name] = previous + declaration
    }

    fun buildName(serviceId: ServiceId): String =
        (nameTransformations[serviceId] ?: { it }).invoke(eventName)

    fun buildProps(serviceId: ServiceId): Map<String, String> =
        properties
            .filter { prop ->
                // remove properties for different services and optionals that don't have values
                when (val spec = prop.value.specForService(serviceId)) {
                    is PropertyDeclaration.Spec.Optional -> {
                        fixedProperties.containsKey(prop.key) || tryProvide(prop.key, spec)
                    }
                    is PropertyDeclaration.Spec.OfValue,
                    is PropertyDeclaration.Spec.Required -> true
                    null -> false // not for this service
                }
            }
            .mapValues {
                when (val spec = it.value.specForService(serviceId)!!) {
                    is PropertyDeclaration.Spec.OfValue -> spec.value
                    is PropertyDeclaration.Spec.Optional -> {
                        // optionals that don't have values were removed in earlier filter() step,
                        // so the value should be available
                        fixedProperties[it.key] ?: providedValues[it.key]!!
                    }
                    is PropertyDeclaration.Spec.Required -> {
                        fixedProperties[it.key] ?: run {
                            tryProvide(it.key, spec)
                            providedValues[it.key]
                                ?: error("Unable to provide value for required property '${it.key}'")
                        }
                    }
                }
            }

    private fun tryProvide(propertyName: String, spec: PropertyDeclaration.Spec): Boolean =
        when (spec) {
            is PropertyDeclaration.Spec.Optional,
            is PropertyDeclaration.Spec.Required -> {
                providedValues.containsKey(propertyName) || run {
                    providedValues.putAll(propertyProvider(propertyName))
                    providedValues.containsKey(propertyName)
                }
            }
            is PropertyDeclaration.Spec.OfValue -> false
        }

    private inner class ServiceSpecificConfiguratorImpl(
        private val serviceId: ServiceId
    ) : ServiceSpecificEventConfigurator {
        override fun name(transformer: (String) -> String) {
            this@EventConfiguratorImpl.nameTransformations[serviceId] = transformer
        }

        override fun set(name: String, value: String) {
            this@EventConfiguratorImpl.addPropertyDeclaration(name, PropertyDeclaration.ofValue(value, serviceId))
        }

        override fun require(name: String) {
            this@EventConfiguratorImpl.addPropertyDeclaration(name, PropertyDeclaration.required(serviceId))
        }

        override fun optional(name: String) {
            this@EventConfiguratorImpl.addPropertyDeclaration(name, PropertyDeclaration.optional(serviceId))
        }
    }
}
