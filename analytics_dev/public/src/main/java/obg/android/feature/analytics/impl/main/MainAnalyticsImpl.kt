package obg.android.feature.analytics.impl.main

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events
import obg.android.feature.analytics.models.OBGAnalyticsTabBarChangeModel

class MainAnalyticsImpl(private val analytics: Analytics) : MainAnalytics {
    override fun logTabBarChanged(
        infoModel: OBGAnalyticsTabBarChangeModel
    ) = analytics.logEvent(
        event = Events.Action.ActionTabBarChange(infoModel)
    )

    override fun logPushNotificationClicked(
        pushNotificationKeyValue: String
    ) = analytics.logEvent(
        event = Events.Action.PushNotificationClickedAction(
            pushNotificationKeyValue = pushNotificationKeyValue
        )
    )

    override fun logNotificationDialog(isAllowed: Boolean) = analytics.logEvent(
        event = Events.Action.NotificationDialogAction(
            isAllowed = isAllowed.toString()
        )
    )

    override fun logOnboardingPopupShown() = analytics.logEvent(
        event = Events.Action.OnboardingPopupShown
    )

    override fun logViewGameFilterAction() =
        analytics.logEvent(
            event = Events.Action.ViewGameFilterAction
        )

    override fun logViewPageWithAccountType(viewName: String) =
        analytics.logEvent(
            event = Events.AccountTypeSimpleViewPage(
                viewName = viewName
            )
        )

    override fun logOpenGameAction() = analytics.logEvent(event = Events.Action.OpenGameAction)

    override fun logOnboardingModuleClickAction(sectionId: String) = analytics.logEvent(
        event = Events.Action.OnboardingModule.Clicked(moduleType = sectionId)
    )

    override fun logRegistrationNextAction() = analytics.logEvent(
        event = Events.Action.RegistrationNextAction
    )

    override fun logDepositBackAction() = analytics.logEvent(
        event = Events.Action.DepositBackAction
    )
}
