package al.bruno.weather.analytics

import al.bruno.weather.analytics.logging.LogLevel
import al.bruno.weather.analytics.logging.LogSink
import al.bruno.weather.analytics.logging.logging
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class HistoryAndLoggingTest {

    private val firebase = ServiceId("Firebase")
    private val amplitude = ServiceId("Amplitude")

    // ---- history ----

    @Test
    fun `history keeps the newest events first and drops the oldest past maxSize`() {
        var now = 0L
        val history = InMemoryAnalyticsHistory(maxSize = 2, clock = { Instant.ofEpochSecond(now++) })

        listOf("a", "b", "c").forEach { history.record(ProcessedEvent(firebase, it, emptyMap())) }

        assertEquals(listOf("c", "b"), history.events().map { it.name })
        assertEquals(Instant.ofEpochSecond(2), history.events().first().time)
    }

    @Test
    fun `search matches names, property keys and values, and filters by service`() {
        val history = InMemoryAnalyticsHistory()
        history.record(ProcessedEvent(firebase, "city_searched", mapOf("city" to "Paris")))
        history.record(ProcessedEvent(amplitude, "city_searched", mapOf("city" to "Rome")))
        history.record(ProcessedEvent(firebase, "app_open", mapOf("source" to "launcher")))

        assertEquals(listOf("city_searched"), history.search("pari").map { it.name })
        assertEquals(2, history.search("CITY").size)
        assertEquals(listOf("app_open"), history.search("launch", setOf(firebase)).map { it.name })
        assertEquals(3, history.search("p").size)                       // too short: no filtering
        assertEquals(1, history.search("", setOf(amplitude)).size)
    }

    @Test
    fun `history records failed events through analytics`() {
        val history = InMemoryAnalyticsHistory()
        val analytics = analytics {
            service(object : GenericAnalyticsService() {
                override val id = firebase
                override fun sendEvent(name: String, properties: Map<String, String>) = Unit
            })
            history(history)
        }

        analytics.logEvent("ok")
        analytics.logEvent("broken") { require("missing") }

        val (broken, ok) = history.events()
        assertEquals("ok", ok.name)
        assertEquals(null, ok.error)
        assertEquals("broken", broken.name)
        assertTrue(broken.error!!.contains("missing"))
    }

    // ---- logging ----

    private class RecordingSink : LogSink {
        val lines = mutableListOf<String>()
        override fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
            lines += "$level/$tag: $message"
        }
    }

    @Test
    fun `messages below the minimum level are never built`() {
        val sink = RecordingSink()
        val log = logging { minLevel = LogLevel.INFO; sink(sink) }.logger("Weather")
        var built = false

        log.d { built = true; "debug" }
        log.w { "careful" }

        assertFalse(built)
        assertEquals(listOf("WARN/Weather: careful"), sink.lines)
    }

    @Test
    fun `every sink receives each line`() {
        val a = RecordingSink()
        val b = RecordingSink()
        logging { sink(a); sink(b) }.logger("T").i { "hello" }

        assertEquals(listOf("INFO/T: hello"), a.lines)
        assertEquals(a.lines, b.lines)
    }

    @Test
    fun `logging without sinks is disabled`() {
        val log = logging { minLevel = LogLevel.VERBOSE }.logger("T")
        assertFalse(log.isEnabled(LogLevel.ERROR))
    }

    @Test
    fun `logEvents interceptor logs sent events at debug`() {
        val sink = RecordingSink()
        val appLogging = logging { sink(sink) }
        val analytics = analytics {
            logging = appLogging
            service(LoggingAnalyticsService(appLogging, level = LogLevel.INFO))
            logEvents()
        }

        analytics.logEvent("city_searched") { set("city", "Paris") }

        assertEquals(
            listOf(
                "INFO/Analytics-Log: city_searched {city=Paris}",
                "DEBUG/Analytics: [Log] city_searched {city=Paris}",
            ),
            sink.lines,
        )
    }
}
