package obg.android.feature.analytics.domain.model

/**
 * Interceptors can be used for logging or triggering additional actions for events before
 * passing them to specific analytics service.
 */
interface EventsInterceptor {
    /**
     * Called for each event.
     */
    fun process(
        eventName: String,
        properties: Map<String, String>,
        serviceId: ServiceId,
        error: String? = null
    )
}
