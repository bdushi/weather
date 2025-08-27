package al.bruno.weather.data.di

import al.bruno.domain.weather.repository.CacheSearchRepository
import al.bruno.domain.weather.repository.LocationRepository
import al.bruno.domain.weather.repository.WeatherRepository
import al.bruno.weather.data.repository.CacheSearchRepositoryImpl
import al.bruno.weather.data.repository.LocationRepositoryImpl
import al.bruno.weather.data.repository.WeatherRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideWeatherRepository(weatherRepository: WeatherRepositoryImpl): WeatherRepository

    @Binds
    abstract fun provideCacheSearchRepository(cacheSearchRepository: CacheSearchRepositoryImpl): CacheSearchRepository

    @Binds
    abstract fun provideLocationRepository(locationRepository: LocationRepositoryImpl): LocationRepository
}