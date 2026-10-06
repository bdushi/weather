package al.bruno.weather.di

import al.bruno.weather.BuildConfig
import al.bruno.weather.analytics.Analytics
import al.bruno.weather.analytics.AnalyticsHistory
import al.bruno.weather.analytics.InMemoryAnalyticsHistory
import al.bruno.weather.analytics.LoggingAnalyticsService
import al.bruno.weather.analytics.analytics
import al.bruno.weather.analytics.logging.LogLevel
import al.bruno.weather.analytics.logging.LogcatSink
import al.bruno.weather.analytics.logging.Logging
import al.bruno.weather.analytics.logging.logging
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
class AnalyticsModule {

    @Single(createdAtStart = false)
    fun logging(): Logging = logging {
        minLevel = if (BuildConfig.DEBUG) LogLevel.DEBUG else LogLevel.WARN
        sink(LogcatSink)
    }

    @Single(createdAtStart = false)
    fun analyticsHistory(): AnalyticsHistory = InMemoryAnalyticsHistory()

    @Single(createdAtStart = false)
    fun analytics(appLogging: Logging, history: AnalyticsHistory): Analytics = analytics {
        logging = appLogging
        if (BuildConfig.DEBUG) {
            // No real backend yet: in debug, events go to Logcat and the in-memory history.
            service(LoggingAnalyticsService(appLogging))
            logEvents()
            history(history)
        }
        properties("app_version", "build_type") {
            mapOf("app_version" to BuildConfig.VERSION_NAME, "build_type" to BuildConfig.BUILD_TYPE)
        }
        redaction { !BuildConfig.DEBUG }
    }
}
