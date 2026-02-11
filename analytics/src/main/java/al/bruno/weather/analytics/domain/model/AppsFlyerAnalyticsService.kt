package al.bruno.weather.analytics.domain.model

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerProperties
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APPSFLYER_CUID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_USER_ID

class AppsFlyerAnalyticsService(
    private val applicationContext: Context
) : GenericAnalyticsService(
    redactedProps = setOf(OBG_PROP_USER_ID, OBG_PROP_APPSFLYER_CUID),
) {
    override val id: ServiceId = Analytics.APPS_FLYER
    private lateinit var instance: AppsFlyerLib
    private lateinit var properties: AppsFlyerProperties

    private val userProperties = mutableMapOf<String, String>()

    override fun initialize() {
        instance = AppsFlyerLib.getInstance()
        properties = AppsFlyerProperties.getInstance()
    }

    override fun sendNativeEvent(name: String, properties: Map<String, String>) {
        require(name.length in 1..MAX_EVENT_NAME_LENGTH) {
            "Event name length must be in [1, $MAX_EVENT_NAME_LENGTH] range"
        }
        instance.logEvent(applicationContext, name, properties)
    }

    override fun setUserProperty(name: String, value: String) {
        userProperties[name] = value
        instance.setAdditionalData(userProperties.toMap())
    }

    override fun setUserId(id: String) {
        if (properties.getString(AppsFlyerProperties.APP_USER_ID).isNullOrEmpty()) {
            instance.setCustomerUserId(id)
        }
    }

    companion object {
        private const val MAX_EVENT_NAME_LENGTH = 100
    }
}
