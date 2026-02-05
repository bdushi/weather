package obg.android.feature.analytics

import android.app.Application
import android.os.Bundle
import obg.android.feature.analytics.models.HomeItemAnalyticsModel
import obg.android.feature.analytics.models.OBGAnalyticsTabBarChangeModel
import obg.android.feature.analytics.utils.KeyValuePair
import obg.android.feature.analytics.utils.KeyValuePairList

abstract class OBGAnalyticsProvider(var identification: String) {

    abstract fun sendLoginEvent(
        event: String,
        method: String,
        vararg additionalParams: KeyValuePair
    )

    abstract fun sendRegisterEvent(
        event: String,
        method: String,
        vararg additionalParams: KeyValuePair
    )

    abstract fun sendHomeItemSelectEvent(
        event: String,
        homeItem: HomeItemAnalyticsModel,
        vararg additionalParams: KeyValuePair
    )

    abstract fun sendTabBarChangedEvent(
        event: String,
        changeTabModel: OBGAnalyticsTabBarChangeModel,
        vararg additionalParams: KeyValuePair
    )

    abstract fun sendViewPageEvent(page: String, list: KeyValuePairList)

    abstract fun sendActionEvent(action: String, list: KeyValuePairList)

    fun beforeSendActionEvent(action: String, list: KeyValuePairList) {
        sendActionEvent(action, list)
    }

    abstract fun init(
        application: Application,
        analyticsProvider: AnalyticsProvider,
        ignoreAnalyticEvents: Boolean
    )
    abstract fun setUserId(userId: String)
    abstract fun setUserIdProperty(userId: String)
    abstract fun setUserProperty(vararg userProperty: KeyValuePair)
    abstract fun logEvent(eventName: String, params: Bundle)
    abstract fun setUserCurrencyProperty(currency: String)
    abstract fun setPrivacyConsentProperty(selection: String)

    companion object {
        protected var TAG = "OBGAnalytics"
    }

    enum class AnalyticsProvider { FIREBASE }
}
