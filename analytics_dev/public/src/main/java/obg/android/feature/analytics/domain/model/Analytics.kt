package obg.android.feature.analytics.domain.model

import obg.android.feature.analytics.domain.model.event.Event

/**
 * Main entry point for interacting with analytics.
 */
interface Analytics {
    /**
     * Initializes all configured analytics services.
     */
    fun initialize()

    /**
     * Passes given [event] for sending to analytics services.
     *
     * The implementation will decide the routing and supplementing the [event] with properties,
     * based on configuration in the [event].
     */
    fun logEvent(event: Event)

    /**
     * Adds "fixed" properties that do not change value.
     *
     * When calling this method multiple times, existing property values may get replaced,
     * but none of the previously added properties will be removed.
     */
    fun setProperties(properties: Map<String, String>)

    /**
     * Sets user property to a specified value.
     *
     * A user property is persisted throughout the app lifecycle and across sessions.
     *
     * @param serviceId to which analytics service this property belongs; null means all services
     */
    fun setUserProperty(name: String, value: String, serviceId: ServiceId? = null)

    /**
     * Sets the user ID, so that it can be matched with analytics internal user ID.
     *
     * @param serviceId to which analytics service this user ID belongs; null means all services
     */
    fun setUserId(id: String, serviceId: ServiceId? = null)

    /**
     * Allows for setting a flag provider that would control when redaction of PII data is enabled.
     *
     * If not set, by default redaction is enabled.
     */
    fun setRedactionEnabler(redactionFlagProvider: () -> Boolean)

    companion object {
        val FIREBASE = ServiceId("Firebase")
        val APPS_FLYER = ServiceId("AppsFlyer")
        val ALL_SERVICES = setOf(FIREBASE, APPS_FLYER)
    }
}
