package obg.android.feature.analytics.domain.usecase

import obg.android.feature.analytics.domain.model.ServiceId
import obg.android.feature.analytics.history.LoggedEvent

interface SearchHistoryUseCase {
    operator fun invoke(query: String, services: Set<ServiceId> = emptySet()): List<LoggedEvent>

    companion object {
        const val MIN_QUERY_LENGTH = 2
    }
}
