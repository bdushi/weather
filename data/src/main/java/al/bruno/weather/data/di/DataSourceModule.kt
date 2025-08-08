package al.bruno.weather.data.di

import al.bruno.weather.data.CacheSearchDataSource
import al.bruno.weather.data.WeatherDataSource
import al.bruno.weather.data.local.CacheSearchLocalDataSource
import al.bruno.weather.data.network.WeatherNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun provideWeatherNetworkDataSource(weatherDataSource: WeatherDataSource): WeatherNetworkDataSource

    @Binds
    abstract fun provideSearchDataSource(searchDataSource: CacheSearchDataSource): CacheSearchLocalDataSource
}