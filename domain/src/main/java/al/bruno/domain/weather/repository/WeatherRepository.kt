package al.bruno.domain.weather.repository

import al.bruno.domain.weather.model.Forecast
import al.bruno.domain.weather.model.Result
import al.bruno.domain.weather.model.Weather

interface WeatherRepository {
    suspend fun weather(query: String) : Result<Weather>
    suspend fun forecast(query: String): Result<Forecast>
}