package al.bruno.weather.data

import al.bruno.weather.data.network.WeatherNetworkDataSource
import al.bruno.weather.data.network.model.ForecastResponse
import al.bruno.weather.data.network.model.WeatherResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class WeatherDataSource @Inject constructor(private val httpClient: HttpClient) : WeatherNetworkDataSource {
    override suspend fun weather(query: String): WeatherResponse {
        return httpClient.get("/data/2.5/weather") {
            url {
                parameter("q", query)
                parameter("appid", BuildConfig.API_KEY)
                parameter("units", "metric")
            }
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun forecast(query: String): ForecastResponse {
        return httpClient.get("/data/2.5/forecast") {
            url {
                parameter("q", query)
                parameter("appid", BuildConfig.API_KEY)
                parameter("units", "metric")
            }
            contentType(ContentType.Application.Json)
        }.body()
    }
}