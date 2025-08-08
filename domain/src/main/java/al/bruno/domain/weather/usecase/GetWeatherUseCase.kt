package al.bruno.domain.weather.usecase

import al.bruno.domain.weather.model.Result
import al.bruno.domain.weather.model.Weather
import al.bruno.domain.weather.repository.WeatherRepository

class GetWeatherUseCase (private val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(query: String): Result<Weather> =
        weatherRepository.weather(query)
}