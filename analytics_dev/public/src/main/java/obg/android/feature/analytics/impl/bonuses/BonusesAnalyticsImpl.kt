package obg.android.feature.analytics.impl.bonuses

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class BonusesAnalyticsImpl(
    private val analytics: Analytics
) : BonusesAnalytics {
    override fun logSimpleClaimAction(
        bonusType: String,
        bonusId: String
    ) = analytics.logEvent(
        event = Events.Action.BonusClaimedAction.Simple(
            bonusType = bonusType,
            bonusId = bonusId
        )
    )

    override fun logLockFundsClaimAction(
        amount: String,
        bonusType: String,
        bonusId: String
    ) = analytics.logEvent(
        event = Events.Action.BonusClaimedAction.LockFunds(
            bonusType = bonusType,
            bonusId = bonusId,
            amount = amount
        )
    )

    override fun logBonusQuickAmount(
        amount: String,
        bonusType: String,
        bonusId: String
    ) = analytics.logEvent(
        event = Events.Action.BonusQuickAmountAction(
            bonusId = bonusId,
            bonusSelectedAmount = amount,
            bonusType = bonusType
        )
    )

    override fun logBonusAdditionalCancelAction(
        bonusId: String,
        amount: String,
        bonusType: String
    ) = analytics.logEvent(
        event = Events.Action.BonusAdditionalCancelAction(
            bonusId = bonusId,
            amount = amount,
            bonusType = bonusType
        )
    )

    override fun logBonusAdditionalOkAction(
        amount: String,
        bonusType: String,
        bonusId: String
    ) = analytics.logEvent(
        event = Events.Action.BonusAdditionalOkAction(
            amount = amount,
            bonusType = bonusType,
            bonusId = bonusId,
        )
    )

    override fun logTocViewedAction(
        bonusId: String,
        bonusType: String,
        bonusState: String
    ) = analytics.logEvent(
        event = Events.Action.BonusTocViewedAction(
            bonusId = bonusId,
            bonusState = bonusState,
            bonusType = bonusType
        )
    )

    override fun logBonusForfeitedAction(
        bonusId: String,
        bonusState: String,
        bonusType: String,
        bonusWagered: String,
        bonusExpDate: String
    ) = analytics.logEvent(
        event = Events.Action.BonusForfeitedAction(
            bonusId = bonusId,
            bonusState = bonusState,
            bonusType = bonusType,
            bonusWagered = bonusWagered,
            bonusExpDate = bonusExpDate
        )
    )

    override fun logBonusCtaClickAction(
        bonusId: String,
        bonusState: String,
        bonusType: String
    ) = analytics.logEvent(
        event = Events.Action.BonusCtaClickAction(
            bonusId = bonusId,
            bonusState = bonusState,
            bonusType = bonusType
        )
    )

    override fun logBonusSeen(
        bonusState: String,
        bonusId: String,
        bonusType: String
    ) = analytics.logEvent(
        event = Events.Action.BonusViewedAction(
            bonusState = bonusState,
            bonusType = bonusType,
            bonusId = bonusId
        )
    )

    override fun logBonusSelectedOnlyDepositAction(
        numberOfBonuses: String
    ) = analytics.logEvent(
        event = Events.Action.BonusSelectOnlyDeposit(numberOfBonuses = numberOfBonuses)
    )
}
