package obg.android.feature.analytics.history

import obg.android.feature.analytics.domain.model.ServiceId
import java.time.LocalDateTime

data class LoggedEvent(
    val dateTime: LocalDateTime,
    val service: ServiceId,
    val eventName: String,
    val properties: Map<String, String>,
    val error: String? = null
)
