package al.bruno.weather.data.network

import al.bruno.weather.data.network.model.ForecastResponse
import al.bruno.weather.data.network.model.WeatherResponse

interface WeatherNetworkDataSource {
    suspend fun weather(query: String) : WeatherResponse
    suspend fun forecast(query: String) : ForecastResponse
}