package obg.android.feature.analytics.domain.model.event

import obg.android.feature.analytics.domain.model.ServiceId

@EventMarker
interface EventConfigurator : PropertyConfigurator {

    /**
     * Set configuration for a specific analytics service.
     */
    fun service(
        serviceId: ServiceId,
        eventConfigurator: ServiceSpecificEventConfigurator.() -> Unit
    )
}
