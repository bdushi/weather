package al.bruno.weather

import al.bruno.walks.di.DispatchersModule
import al.bruno.weather.core.di.CoroutineScopesModule
import al.bruno.weather.di.UseCaseModule
import al.bruno.weather.data.di.CoreModule
import al.bruno.weather.data.di.DataSourceModule
import al.bruno.weather.data.di.LocalModule
import al.bruno.weather.data.di.NetworkModule
import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import io.kotzilla.sdk.analytics.koin.analytics
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.ksp.generated.module

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
        startKoin {
            androidLogger()
            androidContext(this@WeatherApp)
            analytics()
            modules(
                UseCaseModule().module,
                CoreModule().module,
                DataSourceModule().module,
                LocalModule().module,
                NetworkModule().module,
                CoroutineScopesModule().module,
                DispatchersModule().module,
            )
        }
    }
}