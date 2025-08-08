package al.bruno.domain.weather.usecase

import al.bruno.domain.weather.model.Forecast
import al.bruno.domain.weather.model.Result
import al.bruno.domain.weather.repository.WeatherRepository

class GetForecastUseCase(private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(query: String): Result<Forecast> =
        weatherRepository.forecast(query)
}