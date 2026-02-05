package obg.android.feature.analytics.impl.main

import obg.android.feature.analytics.models.OBGAnalyticsTabBarChangeModel

interface MainAnalytics {
    fun logTabBarChanged(infoModel: OBGAnalyticsTabBarChangeModel)
    fun logPushNotificationClicked(pushNotificationKeyValue: String)
    fun logNotificationDialog(isAllowed: Boolean)
    fun logOnboardingPopupShown()
    fun logViewGameFilterAction()
    fun logViewPageWithAccountType(viewName: String)
    fun logOpenGameAction()
    fun logOnboardingModuleClickAction(sectionId: String)
    fun logRegistrationNextAction()
    fun logDepositBackAction()
}
