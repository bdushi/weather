package al.bruno.weather.core.logging

enum class LogLevel { VERBOSE, DEBUG, INFO, WARN, ERROR }

/** Where log lines end up (Logcat, a file, a crash reporter...). */
fun interface LogSink {
    fun log(level: LogLevel, tag: String, message: String, throwable: Throwable?)
}

@DslMarker
annotation class LoggingDsl

/**
 * Logging configuration shared by every [Logger] created from it.
 *
 * ```
 * val logging = logging {
 *     minLevel = LogLevel.DEBUG
 *     sink(LogcatSink)
 * }
 * private val log = logging.logger("Weather")
 * log.d { "Loaded $city" }   // the message is only built when DEBUG is enabled
 * ```
 */
class Logging internal constructor(
    val minLevel: LogLevel,
    private val sinks: List<LogSink>,
) {
    fun logger(tag: String): Logger = Logger(tag, this)

    fun isEnabled(level: LogLevel): Boolean = sinks.isNotEmpty() && level >= minLevel

    internal fun emit(level: LogLevel, tag: String, message: String, throwable: Throwable?) {
        sinks.forEach { it.log(level, tag, message, throwable) }
    }

    companion object {
        /** Logs nothing. Handy default for tests and previews. */
        val NONE: Logging = Logging(LogLevel.ERROR, emptyList())
    }
}

@LoggingDsl
class LoggingBuilder internal constructor() {
    var minLevel: LogLevel = LogLevel.DEBUG
    private val sinks = mutableListOf<LogSink>()

    fun sink(sink: LogSink) {
        sinks += sink
    }

    internal fun build() = Logging(minLevel, sinks.toList())
}

fun logging(block: LoggingBuilder.() -> Unit): Logging = LoggingBuilder().apply(block).build()

/** A tagged logger. Messages are lambdas, so disabled levels cost nothing to build. */
class Logger internal constructor(val tag: String, private val logging: Logging) {

    fun isEnabled(level: LogLevel): Boolean = logging.isEnabled(level)

    inline fun v(throwable: Throwable? = null, message: () -> String) = log(LogLevel.VERBOSE, throwable, message)
    inline fun d(throwable: Throwable? = null, message: () -> String) = log(LogLevel.DEBUG, throwable, message)
    inline fun i(throwable: Throwable? = null, message: () -> String) = log(LogLevel.INFO, throwable, message)
    inline fun w(throwable: Throwable? = null, message: () -> String) = log(LogLevel.WARN, throwable, message)
    inline fun e(throwable: Throwable? = null, message: () -> String) = log(LogLevel.ERROR, throwable, message)

    inline fun log(level: LogLevel, throwable: Throwable? = null, message: () -> String) {
        if (isEnabled(level)) emit(level, message(), throwable)
    }

    @PublishedApi
    internal fun emit(level: LogLevel, message: String, throwable: Throwable?) {
        logging.emit(level, tag, message, throwable)
    }
}
