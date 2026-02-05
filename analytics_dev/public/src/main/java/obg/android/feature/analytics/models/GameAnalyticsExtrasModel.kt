package obg.android.feature.analytics.models

data class GameAnalyticsExtrasModel(
    val gameName: String,
    val gameCategory: List<String>,
    val gtmGameCategory: List<String>,
    val gtmGameName: String,
    val gameProvider: String,
    val gameId: String
)
