package obg.android.feature.analytics.domain.usecase

import obg.android.feature.analytics.domain.model.ServiceId
import obg.android.feature.analytics.history.AnalyticsHistoryRepository
import obg.android.feature.analytics.history.LoggedEvent
import javax.inject.Inject

class SearchHistoryUseCaseImpl @Inject constructor(
    private val repository: AnalyticsHistoryRepository
) : SearchHistoryUseCase {
    /**
     * Filters all logged analytics events.
     *
     * The [query] parameter works only if it's length is at least [SearchHistoryUseCase.MIN_QUERY_LENGTH]. The [services]
     * filter is applied when it is not empty.
     *
     * @param query will include events that have either name, or any property name or value containing this substring
     * @param services will include events that has service included in this set
     */
    override operator fun invoke(query: String, services: Set<ServiceId>): List<LoggedEvent> =
        repository.events()
            .filter {
                if (services.isNotEmpty()) {
                    services.contains(it.service)
                } else {
                    true
                }
            }
            .filter {
                if (query.length >= SearchHistoryUseCase.MIN_QUERY_LENGTH) {
                    searchByName(it, query)
                } else {
                    true
                }
            }

    private fun searchByName(event: LoggedEvent, query: String): Boolean =
        event.eventName.contains(query, ignoreCase = true) || event.properties.any {
            it.key.contains(query, ignoreCase = true) || it.value.contains(query, ignoreCase = true)
        }
}
