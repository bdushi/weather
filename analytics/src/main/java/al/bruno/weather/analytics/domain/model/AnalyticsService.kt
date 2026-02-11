package al.bruno.weather.analytics.domain.model

interface AnalyticsService {
    val id: ServiceId

    /**
     * Prepares the service for sending events.
     */
    fun initialize()

    /**
     * Sends the analytics event.
     *
     * @throws IllegalArgumentException when the name or properties have invalid values
     *
     * @param isRedactionEnabled when true, tells the analytics service to remove any
     * personally identifiable information
     */
    fun logEvent(
        name: String,
        properties: Map<String, String>,
        isRedactionEnabled: Boolean = true
    )

    /**
     * Set user property.
     *
     * Persisted throughout application lifecycle and across sessions.
     */
    fun setUserProperty(name: String, value: String)

    /**
     * Set user ID.
     *
     * For matching with internal analytics user IDs.
     */
    fun setUserId(id: String)
}
