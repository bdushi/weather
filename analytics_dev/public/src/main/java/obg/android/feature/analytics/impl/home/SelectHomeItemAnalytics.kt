package obg.android.feature.analytics.impl.home

import obg.android.feature.analytics.models.HomeItemAnalyticsModel

interface SelectHomeItemAnalytics {
    fun logSelectItemHomeAction(analyticModel: HomeItemAnalyticsModel)
}
