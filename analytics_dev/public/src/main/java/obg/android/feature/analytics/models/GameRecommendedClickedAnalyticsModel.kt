package obg.android.feature.analytics.models

data class GameRecommendedClickedAnalyticsModel(
    val gamePageId: String,
    val gameClickedId: String,
    val gamePageName: String,
    val gameClickedName: String,
    val gameClickedIndex: String
)
