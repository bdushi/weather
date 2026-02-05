package obg.android.feature.analytics.domain.model.event

import obg.android.feature.analytics.domain.model.ServiceId

/**
 * Generic analytics event definition.
 */
interface Event {
    /**
     * The name that will be sent to analytics service.
     */
    val name: String

    /**
     * Indicates that the event is service specific.
     *
     * Null value means the event is shared across all services.
     */
    val serviceId: ServiceId?
        get() = null

    /**
     * Allows to configure event details and parameters using a DSL.
     */
    val configuration: EventConfigurator.() -> Unit
        get() = {}
}

typealias EventConfiguration = EventConfigurator.() -> Unit
typealias ServiceSpecificEventConfiguration = ServiceSpecificEventConfigurator.() -> Unit
