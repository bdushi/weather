package al.bruno.presentation.weather

import al.bruno.domain.weather.model.CacheSearch
import al.bruno.domain.weather.model.Forecast
import al.bruno.domain.weather.model.Result
import al.bruno.domain.weather.model.Weather
import al.bruno.domain.weather.repository.LocationRepository
import al.bruno.domain.weather.usecase.DeleteCacheSearchUseCase
import al.bruno.domain.weather.usecase.GetCacheSearchUseCase
import al.bruno.domain.weather.usecase.GetForecastUseCase
import al.bruno.domain.weather.usecase.GetWeatherUseCase
import al.bruno.domain.weather.usecase.InsertCacheSearchUseCase
import al.bruno.weather.presentation.model.CacheSearchUiModel
import al.bruno.weather.presentation.model.UIState
import al.bruno.weather.presentation.model.mapper.toCacheSearchUiModelList
import al.bruno.weather.presentation.model.mapper.toForecastUiModel
import al.bruno.weather.presentation.model.mapper.toWeatherUiModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getCacheSearchUseCase: GetCacheSearchUseCase,
    private val insertCacheSearchUseCase: InsertCacheSearchUseCase,
    private val deleteCacheSearchUseCase: DeleteCacheSearchUseCase,
    private val locationRepository: LocationRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _weatherUIState: MutableStateFlow<WeatherUIState> =
        MutableStateFlow(WeatherUIState())
    val weatherUIState: StateFlow<WeatherUIState> get() = _weatherUIState.asStateFlow()

    init {

//        getWeather(mapOf("q" to DEFAULT_QUERY))
        getCacheSearch()
    }

    fun getWeatherData() {
        locationRepository.fetchLocation {
            getWeather(mapOf("lat" to it.lat.toString(), "lon" to it.lon.toString()))
        }
    }

    fun getCacheSearch() {
        getCacheSearchUseCase()
            .map { cacheSearches ->
                _weatherUIState.update {
                    it.copy(
                        cacheSearch = cacheSearches.toCacheSearchUiModelList()
                    )
                }
            }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    fun getWeather(query: Map<String, String>) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, exception ->
            _weatherUIState.update {
                it.copy(
                    uIState = UIState.Error(exception.message)
                )
            }
        }) {
            // Launch both API calls concurrently
            val weatherDeferred = async { getWeatherUseCase(query = query) }
            val forecastDeferred = async { getForecastUseCase(query = query) }

            // Await both results
            val weatherResponse = weatherDeferred.await()
            val forecastResponse = forecastDeferred.await()

            // Handle weather response
            when (weatherResponse) {
                is Result.Error -> {
                    _weatherUIState.update {
                        it.copy(
                            uIState = UIState.Error(weatherResponse.error)
                        )
                    }
                    return@launch
                }
                is Result.Success<Weather> -> {
                    // continue to check forecast
                }
            }

            when (forecastResponse) {
                is Result.Error -> {
                    _weatherUIState.update {
                        it.copy(
                            uIState = UIState.Error(forecastResponse.error)
                        )
                    }
                    return@launch
                }
                is Result.Success<Forecast> -> {
                    // Forecast successful, continue
                }
            }
            _weatherUIState.update {
                it.copy(
                    query = weatherResponse.data.name,
                    uIState = UIState.Success,
                    weatherUiModel = weatherResponse.data.toWeatherUiModel(),
                    forecastUiModel = forecastResponse.data.toForecastUiModel()
                )
            }
        }
    }

    private fun MutableStateFlow<WeatherUIState>.updateQuery(query: String) {
        update { it.copy(query = query) }
    }

    private fun WeatherViewModel.executeWeatherSearch(query: String, shouldSave: Boolean = false) {
        _weatherUIState.updateQuery(query)
        getWeather(mapOf("q" to query))
        if (shouldSave) saveSearchQuery(query)
    }

    val processExploreUIEvent: (WeatherUIEvent) -> Unit = { event ->
        when (event) {
            WeatherUIEvent.OnClear ->
                executeWeatherSearch(DEFAULT_QUERY)

            is WeatherUIEvent.OnSelectedItems ->
                executeWeatherSearch(event.query, shouldSave = true)

            is WeatherUIEvent.Search -> {
                if (event.query.isNotEmpty())
                    executeWeatherSearch(event.query, shouldSave = true)
                else
                    executeWeatherSearch(DEFAULT_QUERY, shouldSave = true)
            }

            is WeatherUIEvent.OnQueryChange -> {
                _weatherUIState.updateQuery(event.query)
            }

            is WeatherUIEvent.OnDeleteCacheSearch -> {
                deleteCacheSearchQuery(event.cacheSearchUiModel)
            }

            WeatherUIEvent.OnRetry -> {
                _weatherUIState.updateQuery(DEFAULT_QUERY)
                getWeather(mapOf("q" to DEFAULT_QUERY))
            }
        }
    }

    fun deleteCacheSearchQuery(cacheSearchUiModel: CacheSearchUiModel) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCacheSearchUseCase(
                CacheSearch(
                    id = cacheSearchUiModel.id,
                    query = cacheSearchUiModel.query
                )
            )
        }
    }

    fun saveSearchQuery(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            insertCacheSearchUseCase(
                CacheSearch(
                    id = 0,
                    query = query
                )
            )
        }
    }
}