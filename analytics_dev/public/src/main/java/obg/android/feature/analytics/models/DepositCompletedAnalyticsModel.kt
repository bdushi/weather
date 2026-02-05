package obg.android.feature.analytics.models

data class DepositCompletedAnalyticsModel(
    val paymentType: String,
    val currency: String,
    val amount: String,
    val isFirstDeposit: String,
    val paymentReference: String,
)
