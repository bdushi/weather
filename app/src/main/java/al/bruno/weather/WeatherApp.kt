package al.bruno.weather

import al.bruno.weather.core.analytics.Analytics
import al.bruno.weather.core.analytics.logEvent
import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.plugin.module.dsl.startKoin

@KoinApplication
class WeatherApp : Application(), SingletonImageLoader.Factory {
    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .logger(DebugLogger())
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        startKoin<WeatherApp> {
            androidLogger()
            androidContext(this@WeatherApp)
            analytics()
        }
        get<Analytics>().apply {
            initialize()
            logEvent("app_open") {
                require("app_version")
                require("build_type")
            }
        }
    }
}