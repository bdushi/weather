package al.bruno.weather.data.di

import al.bruno.weather.data.CacheSearchDataSource
import al.bruno.weather.data.WeatherDataSource
import al.bruno.weather.data.local.AppDatabase
import al.bruno.weather.data.local.CacheSearchLocalDataSource
import al.bruno.weather.data.network.WeatherNetworkDataSource
import io.ktor.client.HttpClient
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
@ComponentScan
@Configuration
class DataSourceModule {
    @Single(createdAtStart = false)
    fun provideWeatherNetworkDataSource(httpClient: HttpClient): WeatherNetworkDataSource = WeatherDataSource(httpClient)

    @Single(createdAtStart = false)
    fun provideSearchDataSource(appDatabase: AppDatabase): CacheSearchLocalDataSource = CacheSearchDataSource(appDatabase)
}