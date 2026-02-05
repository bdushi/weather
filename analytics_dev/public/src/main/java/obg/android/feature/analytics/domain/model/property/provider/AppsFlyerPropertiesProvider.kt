package obg.android.feature.analytics.domain.model.property.provider

import android.content.Context
import com.appsflyer.AppsFlyerLib
import obg.android.common.hide.HideUtil
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APPSFLYER_DEV_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APPSFLYER_ID
import obg.android.feature.analytics.domain.model.property.PropertiesProvider

class AppsFlyerPropertiesProvider(
    private val applicationContext: Context
) : PropertiesProvider {
    override val providedProperties = setOf(
        OBG_PROP_APPSFLYER_ID,
        OBG_PROP_APPSFLYER_DEV_KEY,
    )

    override val provide: () -> Map<String, String> = {
        val props = mutableMapOf<String, String>()

        props[OBG_PROP_APPSFLYER_ID] = AppsFlyerLib.getInstance().getAppsFlyerUID(applicationContext) ?: ""
        props[OBG_PROP_APPSFLYER_DEV_KEY] = HideUtil().afKey

        props.toMap()
    }
}
