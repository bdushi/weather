package al.bruno.weather.data.repository

import al.bruno.domain.weather.model.Forecast
import al.bruno.domain.weather.model.Result
import al.bruno.domain.weather.model.Weather
import al.bruno.domain.weather.repository.WeatherRepository
import al.bruno.weather.data.network.WeatherNetworkDataSource
import al.bruno.weather.data.repository.mapper.toForecast
import al.bruno.weather.data.repository.mapper.toWeather
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import org.koin.core.annotation.Single

@Single(createdAtStart = false, binds = [WeatherRepository::class])
class WeatherRepositoryImpl(
    private val weatherNetworkDataSource: WeatherNetworkDataSource
) : WeatherRepository {
    override suspend fun weather(query: Map<String, String>): Result<Weather> {
        return try {
            Result.Success(
                weatherNetworkDataSource
                    .weather(query)
                    .toWeather()
            )
        } catch (ex: ResponseException) {
            Result.Error(ex.response.status.description)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            // No network, timeout, malformed JSON: still a Result, never a crash
            Result.Error(ex.message)
        }
    }

    override suspend fun forecast(query: Map<String, String>): Result<Forecast> {
        return try {
            Result.Success(
                weatherNetworkDataSource
                    .forecast(query)
                    .toForecast()
            )
        } catch (ex: ResponseException) {
            Result.Error(ex.response.status.description)
        } catch (ex: CancellationException) {
            throw ex
        } catch (ex: Exception) {
            // No network, timeout, malformed JSON: still a Result, never a crash
            Result.Error(ex.message)
        }
    }
}
