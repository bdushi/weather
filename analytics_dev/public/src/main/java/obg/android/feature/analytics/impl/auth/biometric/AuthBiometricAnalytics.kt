package obg.android.feature.analytics.impl.auth.biometric

interface AuthBiometricAnalytics {
    fun logBiometricLoginTabClickAction(
        tabName: String,
        hasSavedBiometric: String,
        isRememberMeActivated: String
    )
    fun logBiometricLoginActivationOpenAction(
        hasSavedBiometric: String,
        isRememberMeActivated: String
    )
    fun logBiometricLoginActivateAction(
        hasSavedBiometric: String,
        isRememberMeActivated: String
    )
    fun logBiometricResetPasswordAction(
        hasSavedBiometric: String,
        isRememberMeActivated: String
    )
    fun logBiometricLoginFieldChangeAction(
        hasSavedBiometrics: String,
        isRememberMeActivated: String,
        tab: String,
        fieldChanged: String
    )
    fun logBiometricLoginBankIdOpenAction(
        hasSavedBiometric: String,
        isRememberMeActivated: String
    )
}
