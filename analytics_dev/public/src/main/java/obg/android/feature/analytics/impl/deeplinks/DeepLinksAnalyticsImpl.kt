package obg.android.feature.analytics.impl.deeplinks

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class DeepLinksAnalyticsImpl(
    private val analytics: Analytics
) : DeepLinksAnalytics {

    override fun logDeeplinkHomeCategoryAction(action: String) = analytics.logEvent(
        event = Events.Action.HomeDeepLink.Category(
            action = action
        )
    )

    override fun logDeeplinkHomeMoreAction(action: String) = analytics.logEvent(
        event = Events.Action.HomeDeepLink.More(
            action = action
        )
    )

    override fun logDeeplinkHomeURLAction(action: String) = analytics.logEvent(
        event = Events.Action.HomeDeepLink.Url(
            action = action
        )
    )
}
