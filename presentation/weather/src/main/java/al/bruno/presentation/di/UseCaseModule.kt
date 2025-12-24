package al.bruno.presentation.di

import al.bruno.domain.weather.repository.CacheSearchRepository
import al.bruno.domain.weather.repository.WeatherRepository
import al.bruno.domain.weather.usecase.DeleteCacheSearchUseCase
import al.bruno.domain.weather.usecase.GetCacheSearchUseCase
import al.bruno.domain.weather.usecase.GetForecastUseCase
import al.bruno.domain.weather.usecase.GetWeatherUseCase
import al.bruno.domain.weather.usecase.InsertCacheSearchUseCase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single


@Module
@ComponentScan
class UseCaseModule {
    @Single(createdAtStart = false)
    fun getWeatherUseCaseModule(weatherRepository: WeatherRepository) =
        GetWeatherUseCase(weatherRepository)

    @Single(createdAtStart = false)
    fun getGetForecastUseCase(weatherRepository: WeatherRepository) =
        GetForecastUseCase(weatherRepository)


    @Single(createdAtStart = false)
    fun deleteCacheSearchUseCaseModule(cacheSearchRepository: CacheSearchRepository) =
        DeleteCacheSearchUseCase(cacheSearchRepository)


    @Single(createdAtStart = false)
    fun getCacheSearchUseCaseModule(cacheSearchRepository: CacheSearchRepository) =
        GetCacheSearchUseCase(cacheSearchRepository)


    @Single(createdAtStart = false)
    fun insertCacheSearchUseCaseModule(cacheSearchRepository: CacheSearchRepository) =
        InsertCacheSearchUseCase(cacheSearchRepository)
}