package obg.android.feature.analytics.domain.model

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

class FirebaseAnalyticsService(
    private val applicationContext: Context
) : GenericAnalyticsService() {

    override val id: ServiceId = Analytics.FIREBASE
    private val validatedNames: MutableSet<String> = mutableSetOf()
    private lateinit var instance: FirebaseAnalytics

    override fun initialize() {
        instance = FirebaseAnalytics.getInstance(applicationContext)
    }

    override fun sendNativeEvent(name: String, properties: Map<String, String>) {
        checkNameValid(name)
        instance.logEvent(name, properties.toBundle())
    }

    override fun setUserProperty(name: String, value: String) {
        instance.setUserProperty(name, value)
    }

    override fun setUserId(id: String) {
        instance.setUserId(id)
    }

    private fun checkNameValid(name: String) {
        if (validatedNames.contains(name)) {
            return
        }
        validateName(name)
        validatedNames.add(name)
    }

    private fun validateName(name: String) {
        require(name.length in 1..MAX_NAME_LENGTH) {
            "Event name must have length in [1, $MAX_NAME_LENGTH] range, but was ${name.length}"
        }
        require(NAME_REGEX.matches(name)) {
            "Event name must consist of alphanumeric characters and underscores"
        }
        require(RESERVED_PREFIXES.none { name.startsWith(it) }) {
            "Event name cannot start with a reserved prefix"
        }
    }

    private fun Map<String, String>.toBundle(): Bundle =
        Bundle().apply {
            for (entry in this@toBundle) {
                putString(entry.key, entry.value)
            }
        }

    companion object {
        private const val MAX_NAME_LENGTH = 40
        private val NAME_REGEX = Regex("[a-zA-Z][a-zA-Z0-9_]*")
        private val RESERVED_PREFIXES = listOf(
            "ad_activeview",
            "ad_click",
            "ad_exposure",
            "ad_query",
            "ad_reward",
            "adunit_exposure",
            "app_background",
            "app_clear_data",
            "app_exception",
            "app_remove",
            "app_store_refund",
            "app_store_subscription_cancel",
            "app_store_subscription_convert",
            "app_store_subscription_renew",
            "app_update",
            "app_upgrade",
            "dynamic_link_app_open",
            "dynamic_link_app_update",
            "dynamic_link_first_open",
            "error",
            "first_open",
            "first_visit",
            "in_app_purchase",
            "notification_dismiss",
            "notification_foreground",
            "notification_open",
            "notification_receive",
            "os_update",
            "session_start",
            "session_start_with_rollout",
            "user_engagement",
            "firebase_",
            "google_",
            "ga_"
        )
    }
}
