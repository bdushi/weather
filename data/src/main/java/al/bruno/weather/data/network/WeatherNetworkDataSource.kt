package al.bruno.weather.data.network

import al.bruno.weather.data.network.model.ForecastResponse
import al.bruno.weather.data.network.model.WeatherResponse

interface WeatherNetworkDataSource {
    suspend fun weather(query: Map<String, String>) : WeatherResponse
    suspend fun forecast(query: Map<String, String>) : ForecastResponse
}