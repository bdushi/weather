package obg.android.feature.analytics.impl.responsiblegaming

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class DepositLimitsAnalyticsImpl(
    private val analytics: Analytics
) : DepositLimitsAnalytics {
    override fun logLimitRemoveAction() = analytics.logEvent(
        event = Events.Action.LimitRemoveAction
    )

    override fun logLimitSetAction() = analytics.logEvent(
        event = Events.Action.LimitSeAction
    )
}
