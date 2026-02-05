package obg.android.feature.analytics.impl.bankid

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class BankIdAnalyticsImpl(
    private val analytics: Analytics
) : BankIdAnalytics {
    override fun logBankIdMissingInstallAction() = analytics.logEvent(
        event = Events.Action.BankIdMissingInstallAction
    )

    override fun logBankIdMissingCloseAction() = analytics.logEvent(
        event = Events.Action.BankIdMissingCloseAction
    )

    override fun logBankIdMissing(viewName: String) = analytics.logEvent(
        event = Events.Action.BankIdMissingAction(viewName = viewName)
    )
}
