package obg.android.feature.analytics.impl.home

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.Events
import obg.android.feature.analytics.models.HomeItemAnalyticsModel

class SelectHomeItemAnalyticsImpl(
    private val analytics: Analytics
) : SelectHomeItemAnalytics {
    override fun logSelectItemHomeAction(
        analyticModel: HomeItemAnalyticsModel
    ) = analytics.logEvent(
        event = Events.Action.SelectItemHomeAction(
            analyticModel = analyticModel
        )
    )
}
