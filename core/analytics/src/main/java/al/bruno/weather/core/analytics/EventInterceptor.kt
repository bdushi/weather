package al.bruno.weather.core.analytics

import al.bruno.weather.core.logging.Logging
import java.time.Instant
import java.util.ArrayDeque

/** One event as resolved for one service. [error] is set when it could not be built or sent. */
data class ProcessedEvent(
    val serviceId: ServiceId,
    val name: String,
    val properties: Map<String, String>,
    val error: String? = null,
)

/** Sees every event after it was resolved for a service (logging, history, debugging tools). */
fun interface EventInterceptor {
    fun process(event: ProcessedEvent)
}

/** Logs successfully sent events at DEBUG. Failures are already logged at ERROR by [Analytics]. */
class LoggingInterceptor(logging: Logging) : EventInterceptor {
    private val log = logging.logger("Analytics")

    override fun process(event: ProcessedEvent) {
        if (event.error == null) {
            log.d { "[${event.serviceId.name}] ${event.name} ${event.properties}" }
        }
    }
}

data class LoggedEvent(
    val time: Instant,
    val serviceId: ServiceId,
    val name: String,
    val properties: Map<String, String>,
    val error: String? = null,
)

/** Recent events, e.g. for a debug screen. */
interface AnalyticsHistory {
    fun record(event: ProcessedEvent)

    /** Most recent first. */
    fun events(): List<LoggedEvent>

    fun clear()
}

/** Keeps the last [maxSize] events in memory. Thread-safe. */
class InMemoryAnalyticsHistory(
    private val maxSize: Int = 500,
    private val clock: () -> Instant = Instant::now,
) : AnalyticsHistory {

    init {
        require(maxSize > 0) { "maxSize must be positive" }
    }

    private val events = ArrayDeque<LoggedEvent>()

    override fun record(event: ProcessedEvent) {
        val logged = LoggedEvent(clock(), event.serviceId, event.name, event.properties, event.error)
        synchronized(events) {
            events.addFirst(logged)
            while (events.size > maxSize) events.removeLast()
        }
    }

    override fun events(): List<LoggedEvent> = synchronized(events) { events.toList() }

    override fun clear() = synchronized(events) { events.clear() }
}

/** Records every event into [history]. */
class HistoryInterceptor(private val history: AnalyticsHistory) : EventInterceptor {
    override fun process(event: ProcessedEvent) = history.record(event)
}

const val MIN_SEARCH_QUERY_LENGTH = 2

/**
 * Events whose name, property name or property value contains [query] (ignoring case), optionally
 * limited to [services]. Queries shorter than [MIN_SEARCH_QUERY_LENGTH] match everything.
 */
fun AnalyticsHistory.search(query: String, services: Set<ServiceId> = emptySet()): List<LoggedEvent> =
    events()
        .filter { services.isEmpty() || it.serviceId in services }
        .filter { event ->
            query.length < MIN_SEARCH_QUERY_LENGTH ||
                event.name.contains(query, ignoreCase = true) ||
                event.properties.any { (key, value) ->
                    key.contains(query, ignoreCase = true) || value.contains(query, ignoreCase = true)
                }
        }
