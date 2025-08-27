package al.bruno.domain.weather.repository

import al.bruno.domain.weather.model.Forecast
import al.bruno.domain.weather.model.Result
import al.bruno.domain.weather.model.Weather

interface WeatherRepository {
    suspend fun weather(query: Map<String, String>) : Result<Weather>
    suspend fun forecast(query: Map<String, String>): Result<Forecast>
}