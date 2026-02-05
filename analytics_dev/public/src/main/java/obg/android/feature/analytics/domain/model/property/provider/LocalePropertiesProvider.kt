package obg.android.feature.analytics.domain.model.property.provider

import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_INTERFACE_SITE_LANGUAGE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_LOCALE
import obg.android.feature.analytics.domain.model.property.PropertiesProvider
import java.util.Locale
import javax.inject.Inject

class LocalePropertiesProvider @Inject constructor() : PropertiesProvider {

    override val providedProperties = setOf(OBG_PROP_LOCALE, OBG_PROP_INTERFACE_SITE_LANGUAGE)

    override val provide: () -> Map<String, String> = {
        val props = mutableMapOf<String, String>()

        props[OBG_PROP_LOCALE] = Locale.getDefault().language
        props[OBG_PROP_INTERFACE_SITE_LANGUAGE] = Locale.getDefault().language

        props.toMap()
    }
}
