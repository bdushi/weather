package al.bruno.weather.analytics

import al.bruno.weather.analytics.logging.LogLevel
import al.bruno.weather.analytics.logging.Logging

/** One analytics backend (Firebase, Amplitude, Logcat...). Implementations must be thread-safe. */
interface AnalyticsService {
    val id: ServiceId

    fun initialize() {}

    /**
     * @param isRedactionEnabled when true, remove personally identifiable information
     * @throws IllegalArgumentException when the name or properties are invalid for this backend
     */
    fun logEvent(name: String, properties: Map<String, String>, isRedactionEnabled: Boolean)

    fun setUserProperty(name: String, value: String) {}

    fun setUserId(id: String) {}
}

/**
 * Base for services that add default properties and blank out redacted ones.
 *
 * @param defaultProperties added to every event (event properties win on conflict)
 * @param redactedProperties sent with an empty value while redaction is enabled
 */
abstract class GenericAnalyticsService(
    private val defaultProperties: () -> Map<String, String> = { emptyMap() },
    private val redactedProperties: Set<String> = emptySet(),
) : AnalyticsService {

    final override fun logEvent(name: String, properties: Map<String, String>, isRedactionEnabled: Boolean) {
        val all = defaultProperties() + properties
        val toSend = if (isRedactionEnabled) all.mapValues { (key, value) -> if (key in redactedProperties) "" else value } else all
        sendEvent(name, toSend)
    }

    protected abstract fun sendEvent(name: String, properties: Map<String, String>)
}

/** Sends events to the log. Useful in debug builds and until a real backend is added. */
class LoggingAnalyticsService(
    logging: Logging,
    override val id: ServiceId = ID,
    private val level: LogLevel = LogLevel.INFO,
    redactedProperties: Set<String> = emptySet(),
) : GenericAnalyticsService(redactedProperties = redactedProperties) {

    private val log = logging.logger("Analytics-${id.name}")

    override fun sendEvent(name: String, properties: Map<String, String>) {
        log.log(level) { "$name $properties" }
    }

    override fun setUserProperty(name: String, value: String) {
        log.log(level) { "user property $name=$value" }
    }

    override fun setUserId(id: String) {
        log.log(level) { "user id set" }   // never log the id itself
    }

    companion object {
        val ID = ServiceId("Log")
    }
}
