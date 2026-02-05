package obg.android.feature.analytics

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.installations.FirebaseInstallations
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_ITEM_SECTION_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_ITEM_TITLE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_TAB_BAR_FROM_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_GTM_TAB_BAR_TO_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_CAPTION
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_CTA
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_CTA_TEXT
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_SECTION_ID
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_SECTION_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_TITLE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_TYPE
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_ITEM_VIDEO_URL
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_SIGNIN_METHOD
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_BAR_FROM
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_BAR_FROM_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_BAR_TO
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_TAB_BAR_TO_KEY
import obg.android.feature.analytics.OBGAnalyticsProperties.OBG_PROP_USER_UNIQUE_ID
import obg.android.feature.analytics.domain.model.Analytics
import obg.android.feature.analytics.history.AnalyticsHistoryRepository
import obg.android.feature.analytics.models.HomeItemAnalyticsModel
import obg.android.feature.analytics.models.OBGAnalyticsTabBarChangeModel
import obg.android.feature.analytics.utils.KeyValuePair
import obg.android.feature.analytics.utils.KeyValuePairList
import obg.android.feature.analytics.utils.processTextForGTM
import obg.android.feature.analytics.utils.toListPair
import timber.log.Timber

class OBGAnalyticsFirebaseProvider(
    identification: String,
    private var debugLogging: Boolean,
    private val analyticsHistoryRepository: AnalyticsHistoryRepository
) : OBGAnalyticsProvider(identification) {

    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var ignoreAnalyticEvents: Boolean = false

    override fun sendViewPageEvent(page: String, list: KeyValuePairList) {
        logFirebaseEvent(event = FirebaseAnalytics.Event.SCREEN_VIEW, payload = list.toBundle())
    }

    override fun sendLoginEvent(event: String, method: String, vararg additionalParams: KeyValuePair) {
        val bundle = createBundleWithParams(*additionalParams)
        bundle.putString(OBG_PROP_SIGNIN_METHOD, method)

        logFirebaseEvent(event = event, payload = bundle)
    }

    override fun sendRegisterEvent(event: String, method: String, vararg additionalParams: KeyValuePair) {
        val bundle = createBundleWithParams(*additionalParams)
        bundle.putString(OBG_PROP_SIGNIN_METHOD, method)

        logFirebaseEvent(event = event, payload = bundle)
    }

    override fun sendHomeItemSelectEvent(
        event: String,
        homeItem: HomeItemAnalyticsModel,
        vararg additionalParams: KeyValuePair
    ) {
        val bundle = createBundleWithParams(*additionalParams).apply {
            putString(OBG_PROP_ITEM_CTA, homeItem.itemCTA)
            putString(OBG_PROP_ITEM_CTA_TEXT, homeItem.itemCTAText)
            putString(OBG_PROP_ITEM_CAPTION, homeItem.itemCaption)
            putString(OBG_PROP_ITEM_ID, homeItem.itemId)
            putString(OBG_PROP_ITEM_SECTION_ID, homeItem.itemSectionId)
            putString(OBG_PROP_GTM_ITEM_SECTION_ID, homeItem.itemSectionId.processTextForGTM())
            putString(OBG_PROP_ITEM_SECTION_TYPE, homeItem.itemSectionType)
            putString(OBG_PROP_ITEM_TITLE, homeItem.itemTitle)
            putString(OBG_PROP_GTM_ITEM_TITLE, homeItem.itemTitle.processTextForGTM())
            putString(OBG_PROP_ITEM_TYPE, homeItem.itemType)
            putString(OBG_PROP_ITEM_VIDEO_URL, homeItem.itemVideoUrl)
        }

        logFirebaseEvent(event = event, payload = bundle)
    }

    override fun sendTabBarChangedEvent(
        event: String,
        changeTabModel: OBGAnalyticsTabBarChangeModel,
        vararg additionalParams: KeyValuePair
    ) {
        val bundle = createBundleWithParams(*additionalParams).apply {
            putString(OBG_PROP_TAB_BAR_FROM, changeTabModel.tabBarItemFrom.toString())
            putString(OBG_PROP_TAB_BAR_TO, changeTabModel.tabBarItemTo.toString())
            putString(OBG_PROP_TAB_BAR_FROM_KEY, changeTabModel.tabBarItemFromKey)
            putString(OBG_PROP_TAB_BAR_TO_KEY, changeTabModel.tabBarItemToKey)
            putString(OBG_PROP_GTM_TAB_BAR_FROM_KEY, changeTabModel.tabBarItemFromKey.processTextForGTM())
            putString(OBG_PROP_GTM_TAB_BAR_TO_KEY, changeTabModel.tabBarItemToKey.processTextForGTM())
        }

        logFirebaseEvent(event = event, payload = bundle)
    }

    override fun sendActionEvent(action: String, list: KeyValuePairList) {
        logFirebaseEvent(event = action, payload = list.toBundle())
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics?.setUserId(userId)
    }

    override fun init(
        application: Application,
        analyticsProvider: AnalyticsProvider,
        ignoreAnalyticEvents: Boolean
    ) {
        if (firebaseAnalytics == null) {
            this.ignoreAnalyticEvents = ignoreAnalyticEvents
            this.firebaseAnalytics = FirebaseAnalytics.getInstance(application.applicationContext)
        }
    }

    override fun setUserIdProperty(userId: String) {
        firebaseAnalytics?.setUserProperty(OBGAnalyticsProperties.OBG_PROP_USER_ID, userId)
    }

    override fun setUserProperty(vararg userProperty: KeyValuePair) {
        FirebaseInstallations.getInstance().id.addOnSuccessListener {
            firebaseAnalytics?.setUserProperty(OBG_PROP_USER_UNIQUE_ID, it)
        }
        userProperty.forEach {
            firebaseAnalytics?.setUserProperty(it.key, it.stringValue)
        }
    }

    override fun logEvent(eventName: String, params: Bundle) {
        logFirebaseEvent(event = eventName, payload = params)
    }

    override fun setUserCurrencyProperty(currency: String) {
        firebaseAnalytics?.setUserProperty(OBGAnalyticsProperties.OBG_PROP_CURRENCY, currency)
    }

    override fun setPrivacyConsentProperty(selection: String) {
        firebaseAnalytics?.setUserProperty(OBGAnalyticsProperties.OBG_PROP_CONSENT_PRIVACY_STATUS, selection)
    }

    private fun createBundleWithParams(vararg additionalParams: KeyValuePair): Bundle {
        val bundle = Bundle()
        for (param in additionalParams) {
            bundle.putString(param.key, param.stringValue)
        }

        return bundle
    }

    private fun logFirebaseEvent(event: String, payload: Bundle) {
        if (ignoreAnalyticEvents.not()) {
            firebaseAnalytics?.logEvent(event, payload)
        }

        if (debugLogging) {
            analyticsHistoryRepository.appendEvent(
                Analytics.FIREBASE,
                event,
                payload.toMap()
            )
            Timber.tag(TAG).d("Event name: $event, bundle: ${payload.toListPair()}")
        }
    }

    private fun Bundle.toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        for (key in keySet()) {
            map[key] = getString(key) ?: ""
        }
        return map
    }

    private companion object {
        const val TAG = "FirebaseAnalytics"
    }
}
