package obg.android.feature.analytics.impl.responsiblegaming

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class BetLimitAnalyticsImpl(
    private val analytics: Analytics
) : BetLimitAnalytics {
    override fun logBetLimitSetAction() = analytics.logEvent(
        event = Events.Action.BetActions.LimitSet
    )

    override fun logBetLimitRemoveAction() = analytics.logEvent(
        event = Events.Action.BetActions.LimitRemove
    )
}
