package obg.android.feature.analytics.utils

/**
 * Key Value pair to be used as additional params for custom log calls
 */

class KeyValuePair(val key: String, val value: Any?) {

    /**
     * Returns the value if it exists and is a string or can be coerced to a
     * string. Returns null otherwise.
     *
     * This will return null only if the value does not exist, since all types can have a String
     * representation.
     */
    val stringValue: String?
        get() {
            if (value is String) {
                return value
            } else if (value != null) {
                return value.toString()
            }
            return null
        }

    override fun toString(): String {
        return "($key: $value)"
    }
}
