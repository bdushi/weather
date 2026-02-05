package obg.android.feature.analytics.models

data class RateGameAnalyticsModel(
    val gameCategory: String,
    val gameId: String,
    val gameName: String,
    val gameProvider: String,
    val gameRateValue: String,
    val gtmGameCategory: String,
    val gtmGameName: String,
    val balance: String
)
