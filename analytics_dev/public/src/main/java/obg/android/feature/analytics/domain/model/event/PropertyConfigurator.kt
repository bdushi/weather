package obg.android.feature.analytics.domain.model.event

interface PropertyConfigurator {
    /**
     * Sets property with a specific value.
     */
    fun set(name: String, value: String)

    /**
     * Require a property.
     *
     * It's value is expected to be set at a later time from a provider.
     */
    fun require(name: String)

    /**
     * Set a property to be optional.
     *
     * The value may be passed at a later time from a provider.
     */
    fun optional(name: String)
}
