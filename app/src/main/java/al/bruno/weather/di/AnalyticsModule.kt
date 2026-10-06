package al.bruno.weather.di

import al.bruno.weather.BuildConfig
import al.bruno.weather.core.analytics.Analytics
import al.bruno.weather.core.analytics.AnalyticsHistory
import al.bruno.weather.core.analytics.InMemoryAnalyticsHistory
import al.bruno.weather.core.analytics.LoggingAnalyticsService
import al.bruno.weather.core.analytics.analytics
import al.bruno.weather.core.logging.LogLevel
import al.bruno.weather.core.logging.LogcatSink
import al.bruno.weather.core.logging.Logging
import al.bruno.weather.core.logging.logging
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
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
