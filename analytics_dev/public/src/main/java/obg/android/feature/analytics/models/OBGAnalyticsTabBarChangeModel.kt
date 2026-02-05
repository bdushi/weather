package obg.android.feature.analytics.models

data class OBGAnalyticsTabBarChangeModel(
    val tabBarItemFrom: Int,
    val tabBarItemTo: Int,
    val tabBarItemFromKey: String,
    val tabBarItemToKey: String
)
