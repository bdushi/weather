package obg.android.feature.analytics.impl.search

interface ExploreAnalytics {
    fun logSearchResultClickAction(
        event: String,
        searchPosition: String,
        resultTitle: String
    )
    fun logMenuClickAction(menuName: String)
    fun logMenuQuickLinkAction(menuName: String)
    fun logSearchIntentAction()
}
