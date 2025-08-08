package al.bruno.presentation.di

import al.bruno.domain.weather.repository.CacheSearchRepository
import al.bruno.domain.weather.repository.WeatherRepository
import al.bruno.domain.weather.usecase.DeleteCacheSearchUseCase
import al.bruno.domain.weather.usecase.GetCacheSearchUseCase
import al.bruno.domain.weather.usecase.GetForecastUseCase
import al.bruno.domain.weather.usecase.GetWeatherUseCase
import al.bruno.domain.weather.usecase.InsertCacheSearchUseCase
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class UseCaseModule {
    @Reusable
    @Provides
    fun getWeatherUseCaseModule(weatherRepository: WeatherRepository) =
        GetWeatherUseCase(weatherRepository)

    @Reusable
    @Provides
    fun getGetForecastUseCase(weatherRepository: WeatherRepository) =
        GetForecastUseCase(weatherRepository)


    @Reusable
    @Provides
    fun deleteCacheSearchUseCaseModule(cacheSearchRepository: CacheSearchRepository) =
        DeleteCacheSearchUseCase(cacheSearchRepository)


    @Reusable
    @Provides
    fun getCacheSearchUseCaseModule(cacheSearchRepository: CacheSearchRepository) =
        GetCacheSearchUseCase(cacheSearchRepository)


    @Reusable
    @Provides
    fun insertCacheSearchUseCaseModule(cacheSearchRepository: CacheSearchRepository) =
        InsertCacheSearchUseCase(cacheSearchRepository)
}