package obg.android.feature.analytics.impl.sgadepositlimit

interface SgaDepositLimitAnalytics {
    fun logDepositDailyLimitChangeAction(limit: String)
    fun logDepositWeeklyLimitChangeAction(limit: String)
    fun logDepositMonthlyLimitChangeAction(limit: String)
    fun logDepositLimitSetAction(
        daily: String,
        weekly: String,
        monthly: String,
        error: String
    )
}
