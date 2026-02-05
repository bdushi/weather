package obg.android.feature.analytics.impl.auth.biometric

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class AuthBiometricAnalyticsImpl(
    private val analytics: Analytics
) : AuthBiometricAnalytics {
    override fun logBiometricLoginTabClickAction(
        tabName: String,
        hasSavedBiometric: String,
        isRememberMeActivated: String
    ) = analytics.logEvent(
        event = Events.Action.LoginTabClickAction(
            tabName = tabName,
            isRememberMeActivated = isRememberMeActivated,
            isBiometricEnabled = hasSavedBiometric
        )
    )

    override fun logBiometricLoginActivationOpenAction(
        hasSavedBiometric: String,
        isRememberMeActivated: String
    ) = analytics.logEvent(
        event = Events.Action.BiometricAction.ActivationOpen(
            hasSavedBiometrics = hasSavedBiometric,
            isRememberMeActivated = isRememberMeActivated,
        )
    )

    override fun logBiometricLoginActivateAction(
        hasSavedBiometric: String,
        isRememberMeActivated: String
    ) = analytics.logEvent(
        event = Events.Action.BiometricAction.ActivateLogin(
            hasSavedBiometrics = hasSavedBiometric,
            isRememberMeActivated = isRememberMeActivated,
        )
    )

    override fun logBiometricResetPasswordAction(
        hasSavedBiometric: String,
        isRememberMeActivated: String
    ) = analytics.logEvent(
        event = Events.Action.BiometricAction.ResetPassword(
            hasSavedBiometrics = hasSavedBiometric,
            isRememberMeActivated = isRememberMeActivated,
        )
    )

    override fun logBiometricLoginFieldChangeAction(
        hasSavedBiometrics: String,
        isRememberMeActivated: String,
        tab: String,
        fieldChanged: String
    ) = analytics.logEvent(
        event = Events.Action.LoginFieldChanged(
            hasSavedBiometrics = hasSavedBiometrics,
            isRememberMeActivated = isRememberMeActivated,
            tab = tab,
            fieldChanged = fieldChanged,
        )
    )

    override fun logBiometricLoginBankIdOpenAction(
        hasSavedBiometric: String,
        isRememberMeActivated: String
    ) = analytics.logEvent(
        event = Events.Action.BankIdOpenAction(
            isBiometricEnabled = hasSavedBiometric,
            isRememberMeActivated = isRememberMeActivated
        )
    )
}
