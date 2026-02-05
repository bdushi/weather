package obg.android.feature.analytics.impl.base

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class UserAnalyticsImpl(
    private val analytics: Analytics
) : UserAnalytics {
    override fun setUserIdProperty(userId: String) =
        analytics.logEvent(
            event = Events.Action.SetUserIdAction(
                userId = userId
            )
        )
}
