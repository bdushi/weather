package obg.android.feature.analytics.domain.model.property

interface PropertiesProvider {
    /**
     * Property names that this provider can provide values for.
     *
     * All names should be not empty.
     */
    val providedProperties: Set<String>

    /**
     * Generates a map of property name to property value.
     *
     * Can generate 0 or more properties, but the names cannot be different than the ones
     * declared in [providedProperties]
     */
    val provide: () -> Map<String, String>
}
