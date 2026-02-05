package obg.android.feature.analytics.domain.model

/**
 * Reusable analytics service implementation.
 *
 * Handles default properties and value redaction.
 *
 * @param defaultProps set of properties included with every event
 * @param redactedProps properties which will have value cleared whenever [logEvent] is called
 * with redaction flag set to true
 */
abstract class GenericAnalyticsService(
    private val defaultProps: () -> Map<String, String> = { emptyMap() },
    private val redactedProps: Set<String> = emptySet()
) : AnalyticsService {

    override fun logEvent(name: String, properties: Map<String, String>, isRedactionEnabled: Boolean) {
        val propertiesToSend = (defaultProps() + properties).let { allProps ->
            if (isRedactionEnabled) {
                allProps.mapValues {
                    if (it.key in redactedProps) {
                        ""
                    } else {
                        it.value
                    }
                }
            } else {
                allProps
            }
        }

        sendNativeEvent(name, propertiesToSend)
    }

    abstract fun sendNativeEvent(name: String, properties: Map<String, String>)
}
