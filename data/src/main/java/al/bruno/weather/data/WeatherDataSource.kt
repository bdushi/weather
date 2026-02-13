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
import org.koin.core.annotation.Single

@Single(createdAtStart = false, binds = [WeatherNetworkDataSource::class])
class WeatherDataSource(private val httpClient: HttpClient) : WeatherNetworkDataSource {
    override suspend fun weather(query: Map<String, String>): WeatherResponse {
        return httpClient.get("/data/2.5/weather") {
            query.forEach { (key, value) ->
                parameter(key, value)
            }
            parameter("appid", BuildConfig.API_KEY)
            parameter("units", "metric")
            contentType(ContentType.Application.Json)
        }.body()
    }

    override suspend fun forecast(query: Map<String, String>): ForecastResponse {
        return httpClient.get("/data/2.5/forecast") {
            query.forEach { (key, value) ->
                parameter(key, value)
            }
            parameter("appid", BuildConfig.API_KEY)
            parameter("units", "metric")
            contentType(ContentType.Application.Json)
        }.body()
    }
}