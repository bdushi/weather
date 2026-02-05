package obg.android.feature.analytics.history

import obg.android.feature.analytics.domain.model.ServiceId

interface AnalyticsHistoryRepository {
    /**
     * Store single event data.
     */
    fun appendEvent(
        service: ServiceId,
        eventName: String,
        eventProperties: Map<String, String>
    )

    /**
     * Store failed event data.
     */
    fun appendFailedEvent(
        service: ServiceId,
        eventName: String,
        error: String
    )

    /**
     * Removes all stored events.
     */
    fun clear()

    /**
     * Returns the list of all analytic events logged so far.
     *
     * The list is ordered starting from the most recent event.
     */
    fun events(): List<LoggedEvent>
}
