package obg.android.feature.analytics.impl.bonuses

interface BonusesAnalytics {
    fun logSimpleClaimAction(bonusType: String, bonusId: String)
    fun logLockFundsClaimAction(
        amount: String,
        bonusType: String,
        bonusId: String
    )

    fun logBonusQuickAmount(
        amount: String,
        bonusType: String,
        bonusId: String
    )

    fun logBonusAdditionalCancelAction(
        bonusId: String,
        amount: String,
        bonusType: String
    )

    fun logBonusAdditionalOkAction(
        amount: String,
        bonusType: String,
        bonusId: String
    )

    fun logTocViewedAction(
        bonusId: String,
        bonusType: String,
        bonusState: String
    )

    fun logBonusForfeitedAction(
        bonusId: String,
        bonusState: String,
        bonusType: String,
        bonusWagered: String,
        bonusExpDate: String
    )

    fun logBonusCtaClickAction(
        bonusId: String,
        bonusState: String,
        bonusType: String
    )

    fun logBonusSeen(
        bonusState: String,
        bonusId: String,
        bonusType: String
    )

    fun logBonusSelectedOnlyDepositAction(
        numberOfBonuses: String
    )
}
