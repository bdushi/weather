package obg.android.feature.analytics.models

data class StatsClickAnalyticsModel(
    val categoryId: String,
    val categoryName: String,
    val eventId: String,
    val eventPhase: String,
    val competitionId: String,
    val competitionName: String,
    val provider: String
)
