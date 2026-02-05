package obg.android.feature.analytics.data

import obg.android.common.di.TimeProvider
import obg.android.feature.analytics.domain.model.ServiceId
import obg.android.feature.analytics.history.AnalyticsHistoryRepository
import obg.android.feature.analytics.history.LoggedEvent
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Stores all analytics events in an in-memory store.
 */
class AnalyticsHistoryRepositoryImpl @Inject constructor(
    @TimeProvider private val currentDateTime: () -> LocalDateTime
) : AnalyticsHistoryRepository {

    private val history = mutableListOf<LoggedEvent>()

    override fun appendEvent(
        service: ServiceId,
        eventName: String,
        eventProperties: Map<String, String>
    ) {
        history.add(
            LoggedEvent(
                currentDateTime(),
                service,
                eventName,
                eventProperties
            )
        )
    }

    override fun appendFailedEvent(service: ServiceId, eventName: String, error: String) {
        history.add(
            LoggedEvent(
                currentDateTime(),
                service,
                eventName,
                emptyMap(),
                error
            )
        )
    }

    override fun clear() {
        history.clear()
    }

    override fun events(): List<LoggedEvent> = history.reversed()
}
