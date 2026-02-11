package al.bruno.weather.analytics.domain.model

import obg.android.feature.analytics.history.AnalyticsHistoryRepository
import javax.inject.Inject

class EventHistoryInterceptor @Inject constructor(
    private val historyRepository: AnalyticsHistoryRepository
) : EventsInterceptor {

    override fun process(
        eventName: String,
        properties: Map<String, String>,
        serviceId: ServiceId,
        error: String?
    ) {
        if (error != null) {
            historyRepository.appendFailedEvent(
                serviceId,
                eventName,
                error
            )
        } else {
            historyRepository.appendEvent(
                serviceId,
                eventName,
                properties
            )
        }
    }
}
