package obg.android.feature.analytics.models

data class SportsBookWidgetItemClickAnalyticsModel(
    val categoryId: String,
    val categoryName: String,
    val eventId: String,
    val eventName: String,
    val action: String,
    val location: String,
    val betType: String,
    val rememberStakeFlag: String,
)
