package obg.android.feature.analytics.impl.search

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class ExploreAnalyticsImpl(
    private val analytics: Analytics
) : ExploreAnalytics {
    override fun logSearchResultClickAction(
        event: String,
        searchPosition: String,
        resultTitle: String
    ) = analytics.logEvent(
        event = Events.Action.SearchResultClickAction(
            event = event,
            searchPosition = searchPosition,
            resultTitle = resultTitle
        )
    )

    override fun logMenuClickAction(menuName: String) = analytics.logEvent(
        event = Events.Action.MenuAction.Click(
            menuName = menuName
        )
    )

    override fun logMenuQuickLinkAction(menuName: String) = analytics.logEvent(
        event = Events.Action.MenuAction.QuickLink(
            menuName = menuName
        )
    )

    override fun logSearchIntentAction() = analytics.logEvent(
        event = Events.Action.SearchIntentAction
    )
}
