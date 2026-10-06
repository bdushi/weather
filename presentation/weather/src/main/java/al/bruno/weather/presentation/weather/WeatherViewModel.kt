package al.bruno.weather.presentation.weather

import al.bruno.domain.weather.model.CacheSearch
import al.bruno.domain.weather.model.Result
import al.bruno.domain.weather.repository.LocationRepository
import al.bruno.domain.weather.usecase.DeleteCacheSearchUseCase
import al.bruno.domain.weather.usecase.GetCacheSearchUseCase
import al.bruno.domain.weather.usecase.GetForecastUseCase
import al.bruno.domain.weather.usecase.GetWeatherUseCase
import al.bruno.domain.weather.usecase.InsertCacheSearchUseCase
import al.bruno.weather.core.viewmodel.MviViewModel
import al.bruno.weather.presentation.model.mapper.toCacheSearchUiModelList
import al.bruno.weather.presentation.model.mapper.toForecastUiModel
import al.bruno.weather.presentation.model.mapper.toWeatherUiModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

/**
 * Executor for the weather screen. Every decision lives in [WeatherReducer]; this class only turns
 * [WeatherCommand]s into use-case calls and their results into [WeatherUIEvent]s.
 */
@KoinViewModel
class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getCacheSearchUseCase: GetCacheSearchUseCase,
    private val insertCacheSearchUseCase: InsertCacheSearchUseCase,
    private val deleteCacheSearchUseCase: DeleteCacheSearchUseCase,
    private val locationRepository: LocationRepository,
) : MviViewModel<WeatherUIState, WeatherUIEvent, WeatherUIEffect, WeatherCommand>(
    initialState = WeatherUIState(),
    reducer = WeatherReducer,
    debugChecks = BuildConfig.DEBUG,
) {

    init {
        sendEvent(WeatherUIEvent.Started)
    }

    override fun execute(command: WeatherCommand) {
        when (command) {
            WeatherCommand.ObserveHistory -> launchUnique(WeatherCommand.ObserveHistory) {
                getCacheSearchUseCase()
                    .catch { e -> sendEvent(WeatherUIEvent.HistoryFailed(e.message)) }
                    .collect { sendEvent(WeatherUIEvent.HistoryChanged(it.toCacheSearchUiModelList())) }
            }

            is WeatherCommand.ResolveLocation -> try {
                locationRepository.fetchLocation { coord ->
                    sendEvent(WeatherUIEvent.LocationResolved(command.requestId, lat = coord.lat, lon = coord.lon))
                }
            } catch (e: SecurityException) {
                sendEvent(WeatherUIEvent.WeatherFailed(command.requestId, e.message))
            }

            is WeatherCommand.FetchWeather -> fetchWeather(command)

            is WeatherCommand.SaveQuery -> viewModelScope.launch {
                try {
                    insertCacheSearchUseCase(CacheSearch(id = 0, query = command.query))
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    // Not critical for the user: the search itself succeeded.
                }
            }

            is WeatherCommand.DeleteQuery -> viewModelScope.launch {
                try {
                    deleteCacheSearchUseCase(CacheSearch(id = command.item.id, query = command.item.query))
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    sendEvent(WeatherUIEvent.DeleteFailed(command.item, e.message))
                }
            }
        }
    }

    private fun fetchWeather(command: WeatherCommand.FetchWeather) = launchUnique(FETCH_WEATHER) {
        val params = command.target.toParams()
        val event = try {
            val (weather, forecast) = coroutineScope {
                val weather = async { getWeatherUseCase(params) }
                val forecast = async { getForecastUseCase(params) }
                weather.await() to forecast.await()
            }
            if (weather is Result.Success && forecast is Result.Success) {
                WeatherUIEvent.WeatherLoaded(
                    requestId = command.requestId,
                    cityName = weather.data.name,
                    weather = weather.data.toWeatherUiModel(),
                    forecast = forecast.data.toForecastUiModel(),
                )
            } else {
                WeatherUIEvent.WeatherFailed(
                    requestId = command.requestId,
                    message = (weather as? Result.Error)?.error ?: (forecast as? Result.Error)?.error,
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            WeatherUIEvent.WeatherFailed(command.requestId, e.message)
        }
        sendEvent(event)
    }

    private fun WeatherTarget.toParams(): Map<String, String> = when (this) {
        is WeatherTarget.City -> mapOf("q" to name)
        is WeatherTarget.Coordinates -> mapOf("lat" to lat.toString(), "lon" to lon.toString())
    }

    private companion object {
        const val FETCH_WEATHER = "fetchWeather"
    }
}
