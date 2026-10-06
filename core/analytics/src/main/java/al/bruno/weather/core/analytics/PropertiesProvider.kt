package al.bruno.weather.core.analytics

/**
 * Supplies values for properties that events `require` or mark `optional`.
 *
 * Called lazily, only when an event needs one of [providedProperties], and at most once per event.
 */
interface PropertiesProvider {
    /** Property names this provider can supply. Must be non-empty and non-blank. */
    val providedProperties: Set<String>

    /** Values for some or all of [providedProperties]. Other keys are dropped. */
    fun provide(): Map<String, String>
}

/** `propertiesProvider("app_version") { mapOf("app_version" to BuildConfig.VERSION_NAME) }` */
fun propertiesProvider(vararg names: String, values: () -> Map<String, String>): PropertiesProvider =
    object : PropertiesProvider {
        override val providedProperties: Set<String> = names.toSet()
        override fun provide(): Map<String, String> = values()
    }
