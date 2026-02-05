package obg.android.feature.analytics.impl.base

import obg.android.feature.analytics.domain.model.ServiceId

interface UserPropertyAnalytics {
    fun setUserProperty(
        name: String,
        value: String,
        serviceId: ServiceId? = null
    )
}
