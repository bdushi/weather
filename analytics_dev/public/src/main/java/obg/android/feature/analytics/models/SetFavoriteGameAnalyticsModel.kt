package obg.android.feature.analytics.models

data class SetFavoriteGameAnalyticsModel(
    val isFavorite: Boolean,
    val gameCategory: List<String>,
    val gameId: String,
    val gameName: String,
    val balance: String
)
