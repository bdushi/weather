package al.bruno.weather.analytics.domain.model.event

import obg.android.feature.analytics.domain.model.ServiceId
import kotlin.collections.putAll

class PropertyDeclaration private constructor(
    vararg initialSpecs: Pair<ServiceId?, Spec>
) {
    private val specs: MutableMap<ServiceId?, Spec> = mutableMapOf()

    init {
        specs.putAll(initialSpecs)
    }

    /**
     * Gives property specification for specific [serviceId].
     *
     * @return specification, or null if the property is not intended for this [serviceId]
     */
    fun specForService(serviceId: ServiceId): Spec? = specs[serviceId] ?: specs[null]

    fun isForService(serviceId: ServiceId): Boolean = specs.containsKey(serviceId) || specs.containsKey(null)

    operator fun plus(other: PropertyDeclaration): PropertyDeclaration {
        specs.putAll(other.specs)
        return this
    }

    sealed interface Spec {
        object Required : Spec
        object Optional : Spec
        data class OfValue(val value: String) : Spec
    }

    companion object {
        fun ofValue(value: String, serviceId: ServiceId? = null) =
            PropertyDeclaration(serviceId to Spec.OfValue(value))
        fun required(serviceId: ServiceId? = null) = PropertyDeclaration(serviceId to Spec.Required)
        fun optional(serviceId: ServiceId? = null) = PropertyDeclaration(serviceId to Spec.Optional)
        fun empty() = PropertyDeclaration()
    }
}
