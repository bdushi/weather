package obg.android.feature.analytics.models

data class GamePlayAnalyticsExtrasModel(
    val gameName: String,
    val gameCategory: String,
    val gtmGameCategory: String,
    val gtmGameName: String,
    val gameProvider: String,
    val gameId: String
)
