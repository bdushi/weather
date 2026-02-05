package obg.android.feature.analytics.impl.bankid

interface BankIdAnalytics {
    fun logBankIdMissingInstallAction()
    fun logBankIdMissingCloseAction()
    fun logBankIdMissing(viewName: String)
}
