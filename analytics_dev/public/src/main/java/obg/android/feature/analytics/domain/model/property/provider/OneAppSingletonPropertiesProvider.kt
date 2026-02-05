package obg.android.feature.analytics.domain.model.property.provider

import android.os.Build
import obg.android.common.OneAppSingleton
import obg.android.feature.analytics.BuildConfig
import obg.android.feature.analytics.OBGAnalyticsProperties.Native_App_Name
import obg.android.feature.analytics.OBGAnalyticsProperties.Native_App_Version
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_APP_PRODUCT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_BRAND_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_DEVICE_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TECHNICAL_RAW_USER_AGENT_INFO
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_VERSION_NAME
import obg.android.feature.analytics.domain.model.property.PropertiesProvider
import obg.android.shared.domain.model.DeviceType
import javax.inject.Inject

/**
 * Provides all properties extracted from [OneAppSingleton].
 */
class OneAppSingletonPropertiesProvider @Inject constructor() : PropertiesProvider {
    override val providedProperties = setOf(
        OBG_PROP_DEVICE_TYPE,
        OBG_PROP_BRAND_ID,
        OBG_PROP_APP_PRODUCT,
        OBG_PROP_VERSION_NAME,
        Native_App_Version,
        Native_App_Name,
        OBG_PROP_TECHNICAL_RAW_USER_AGENT_INFO
    )

    override val provide: () -> Map<String, String> = {
        val props = mutableMapOf<String, String>()

        val userAgentInfo = "${BuildConfig.FLAVOR}/${OneAppSingleton.versionName} " +
            "(${Build.DEVICE}(${Build.VERSION.RELEASE})) (${OneAppSingleton.applicationId}) Mobile"

        with(OneAppSingleton) {
            if (deviceType != DeviceType.UNDEFINED) {
                props[OBG_PROP_DEVICE_TYPE] = deviceType.name.lowercase()
            }
            props[OBG_PROP_BRAND_ID] = gaProductIdentifier
            props[OBG_PROP_APP_PRODUCT] = currentProduct.name.lowercase()
            props[OBG_PROP_VERSION_NAME] = versionName
            props[Native_App_Version] = versionName
            props[Native_App_Name] = applicationId
            props[OBG_PROP_TECHNICAL_RAW_USER_AGENT_INFO] = userAgentInfo
        }

        props.toMap()
    }
}
