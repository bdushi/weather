package al.bruno.weather.core.logging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class LoggingTest {

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
}
