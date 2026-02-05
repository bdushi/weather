package obg.android.feature.analytics.impl.base

import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.domain.model.ServiceId

class UserPropertyAnalyticsImpl(
    private val analytics: Analytics
) : UserPropertyAnalytics {
    override fun setUserProperty(name: String, value: String, serviceId: ServiceId?) {
        analytics.setUserProperty(
            name = name,
            value = value,
            serviceId = serviceId
        )
    }
}
