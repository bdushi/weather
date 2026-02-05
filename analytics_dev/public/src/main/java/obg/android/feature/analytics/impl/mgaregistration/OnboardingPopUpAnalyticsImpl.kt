package obg.android.feature.analytics.impl.mgaregistration

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events

class OnboardingPopUpAnalyticsImpl(
    private val analytics: Analytics
) : OnboardingPopUpAnalytics {
    override fun logOnBoardingPopUpCtaSkipAction() = analytics.logEvent(
        event = Events.Action.OnBoardingPopUpCtaAction.Skip
    )

    override fun logOnBoardingPopUpLaterClaimAction() = analytics.logEvent(
        event = Events.Action.OnBoardingPopUpCtaAction.Claim
    )
}
