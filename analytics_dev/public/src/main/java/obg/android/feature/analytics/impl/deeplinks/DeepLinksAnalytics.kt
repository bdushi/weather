package obg.android.feature.analytics.impl.deeplinks

interface DeepLinksAnalytics {
    fun logDeeplinkHomeCategoryAction(action: String)
    fun logDeeplinkHomeMoreAction(action: String)
    fun logDeeplinkHomeURLAction(action: String)
}
