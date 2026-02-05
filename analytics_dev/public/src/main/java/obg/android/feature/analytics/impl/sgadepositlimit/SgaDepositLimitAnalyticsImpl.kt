package obg.android.feature.analytics.impl.sgadepositlimit

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class SgaDepositLimitAnalyticsImpl(
    private val analytics: Analytics
) : SgaDepositLimitAnalytics {
    override fun logDepositDailyLimitChangeAction(limit: String) = analytics.logEvent(
        event = Events.Action.DepositAction.DailyLimitChange(
            limit = limit
        )
    )

    override fun logDepositWeeklyLimitChangeAction(limit: String) = analytics.logEvent(
        event = Events.Action.DepositAction.WeeklyLimitChange(
            limit = limit
        )
    )

    override fun logDepositMonthlyLimitChangeAction(limit: String) = analytics.logEvent(
        event = Events.Action.DepositAction.MonthlyLimitChange(
            limit = limit
        )
    )

    override fun logDepositLimitSetAction(
        daily: String,
        weekly: String,
        monthly: String,
        error: String
    ) = analytics.logEvent(
        event = Events.Action.DepositAction.LimitSet(
            daily = daily,
            weekly = weekly,
            monthly = monthly,
            error = error
        )
    )
}
