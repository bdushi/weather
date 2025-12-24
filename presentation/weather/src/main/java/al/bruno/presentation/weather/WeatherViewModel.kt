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
import al.bruno.presentation.ui.base.BaseReducerViewModel
import al.bruno.presentation.ui.base.Reducer
import al.bruno.weather.presentation.model.CacheSearchUiModel
import al.bruno.weather.presentation.model.UIState
import al.bruno.weather.presentation.model.mapper.toCacheSearchUiModelList
import al.bruno.weather.presentation.model.mapper.toForecastUiModel
import al.bruno.weather.presentation.model.mapper.toWeatherUiModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getCacheSearchUseCase: GetCacheSearchUseCase,
    private val insertCacheSearchUseCase: InsertCacheSearchUseCase,
    private val deleteCacheSearchUseCase: DeleteCacheSearchUseCase,
    private val locationRepository: LocationRepository
) : BaseReducerViewModel<WeatherUIState, WeatherUIEvent, WeatherUIEffect>(
    initialState = WeatherUIState()
) {

    /**
     * REDUCER: Pure state transformations + declarative UI effects
     */
    override val reducer = object : Reducer<WeatherUIState, WeatherUIEvent, WeatherUIEffect> {
        override fun reduce(
            state: WeatherUIState,
            event: WeatherUIEvent
        ): Pair<WeatherUIState, WeatherUIEffect?> = when (event) {

            is WeatherUIEvent.Search -> {
                when {
                    event.query.isBlank() -> {
                        // Show validation error
                        state to WeatherUIEffect.ShowError("Search query cannot be empty")
                    }

                    event.query.length < 2 -> {
                        // Show validation error
                        state to WeatherUIEffect.ShowError("Search query must be at least 2 characters")
                    }

                    else -> {
                        // Valid search - set loading state
                        state.copy(
                            query = event.query,
                            isSearching = true,
                            uIState = UIState.Loading
                        ) to null
                    }
                }
            }

            is WeatherUIEvent.OnQueryChange -> {
                // Just update query, no side effect
                state.copy(query = event.query) to null
            }

            is WeatherUIEvent.OnSelectedItems -> {
                // User selected from cache - start search
                state.copy(
                    query = event.query,
                    isSearching = true,
                    uIState = UIState.Loading
                ) to null
            }

            WeatherUIEvent.OnClear -> {
                // Clear search and reset to default
                state.copy(
                    query = DEFAULT_QUERY,
                    isSearching = true,
                    uIState = UIState.Loading
                ) to null
            }

            is WeatherUIEvent.OnDeleteCacheSearch -> {
                // Optimistic update - remove from list
                val newList = state.cacheSearch.filter {
                    it.id != event.cacheSearchUiModel.id
                }

                // Show toast if last item was deleted
                val effect = if (newList.isEmpty()) {
                    WeatherUIEffect.ShowToast("All search history cleared")
                } else {
                    null
                }

                state.copy(cacheSearch = newList) to effect
            }

            WeatherUIEvent.OnRetry -> {
                // Retry with current query
                state.copy(
                    isSearching = true,
                    uIState = UIState.Loading
                ) to null
            }
        }
    }

    override suspend fun onStart() {
        observeCacheSearches()
        fetchWeatherForDefaultLocation()
    }

    override fun sendEvent(event: WeatherUIEvent) {
        super.sendEvent(event)  // Updates state via reducer first

        // Then trigger async operations based on event
        when (event) {
            is WeatherUIEvent.Search -> {
                if (event.query.isNotBlank() && event.query.length >= 2) {
                    performSearch(event.query, shouldSave = true)
                }
            }

            is WeatherUIEvent.OnSelectedItems -> {
                performSearch(event.query, shouldSave = false) // Already in cache
            }

            WeatherUIEvent.OnClear -> {
                performSearch(DEFAULT_QUERY, shouldSave = true)
            }

            WeatherUIEvent.OnRetry -> {
                performSearch(currentState.query, shouldSave = false)
            }

            is WeatherUIEvent.OnDeleteCacheSearch -> {
                deleteCacheItemFromDb(event.cacheSearchUiModel)
            }

            is WeatherUIEvent.OnQueryChange -> {
                // No side effect - just state update
            }
        }
    }

    /**
     * Observes cache searches from database
     */
    private fun observeCacheSearches() {
        getCacheSearchUseCase()
            .onEach { cacheSearches ->
                setState {
                    copy(cacheSearch = cacheSearches.toCacheSearchUiModelList())
                }
            }
            .catch { e ->
                setEffect { WeatherUIEffect.ShowError("Failed to load search history: ${e.message}") }
            }
            .launchIn(viewModelScope)
    }

    /**
     * Fetches weather for user's current location
     */
    private fun fetchWeatherForDefaultLocation() {
        locationRepository.fetchLocation { location ->
            performSearchByCoordinates(location.lat, location.lon)
        }
    }

    /**
     * Fetches weather data by location coordinates
     */
    private fun performSearchByCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                setState {
                    copy(isSearching = true, uIState = UIState.Loading)
                }

                val weatherDeferred = async {
                    getWeatherUseCase(
                        mapOf(
                            "lat" to lat.toString(),
                            "lon" to lon.toString()
                        )
                    )
                }
                val forecastDeferred = async {
                    getForecastUseCase(
                        mapOf(
                            "lat" to lat.toString(),
                            "lon" to lon.toString()
                        )
                    )
                }

                val weatherResult = weatherDeferred.await()
                val forecastResult = forecastDeferred.await()

                handleWeatherResults(weatherResult, forecastResult)
            } catch (e: Exception) {
                setState {
                    copy(
                        isSearching = false,
                        uIState = UIState.Error(e.message)
                    )
                }
                setEffect {
                    WeatherUIEffect.ShowError(e.message ?: "Failed to fetch weather")
                }
            }
        }
    }

    /**
     * Performs weather search by query
     */
    private fun performSearch(query: String, shouldSave: Boolean) {
        viewModelScope.launch {
            try {
                // Parallel API calls
                val weatherDeferred = async {
                    getWeatherUseCase(mapOf("q" to query))
                }
                val forecastDeferred = async {
                    getForecastUseCase(mapOf("q" to query))
                }

                val weatherResult = weatherDeferred.await()
                val forecastResult = forecastDeferred.await()

                handleWeatherResults(weatherResult, forecastResult)

                // Save successful search to cache
                if (weatherResult is Result.Success && shouldSave) {
                    saveSearchQuery(query)
                }
            } catch (e: Exception) {
                setState {
                    copy(
                        isSearching = false,
                        uIState = UIState.Error(e.message)
                    )
                }
                setEffect {
                    WeatherUIEffect.ShowError(e.message ?: "Failed to fetch weather")
                }
            }
        }
    }

    /**
     * Handles weather and forecast results
     */
    private fun handleWeatherResults(
        weatherResult: Result<Weather>,
        forecastResult: Result<Forecast>
    ) {
        when {
            weatherResult is Result.Success<Weather> && forecastResult is Result.Success<Forecast> -> {
                setState {
                    copy(
                        isSearching = false,
                        query = weatherResult.data.name,
                        weatherUiModel = weatherResult.data.toWeatherUiModel(),
                        forecastUiModel = forecastResult.data.toForecastUiModel(),
                        uIState = UIState.Success
                    )
                }
            }

            else -> {
                val error = (weatherResult as? Result.Error)?.error
                    ?: (forecastResult as? Result.Error)?.error

                setState {
                    copy(
                        isSearching = false,
                        uIState = UIState.Error(error)
                    )
                }
                setEffect {
                    WeatherUIEffect.ShowError(
                        error ?: "Failed to fetch weather data"
                    )
                }
            }
        }
    }

    /**
     * Saves search query to cache
     */
    private suspend fun saveSearchQuery(query: String) {
        try {
            insertCacheSearchUseCase(CacheSearch(id = 0, query = query))
        } catch (e: Exception) {
            // Silently fail - not critical for user experience
        }
    }

    /**
     * Deletes cache item from database
     */
    private fun deleteCacheItemFromDb(cacheSearchUiModel: CacheSearchUiModel) {
        viewModelScope.launch {
            try {
                deleteCacheSearchUseCase(
                    CacheSearch(
                        id = cacheSearchUiModel.id,
                        query = cacheSearchUiModel.query
                    )
                )
            } catch (e: Exception) {
                // Revert optimistic update on failure
                setState {
                    copy(cacheSearch = cacheSearch + cacheSearchUiModel)
                }
                setEffect {
                    WeatherUIEffect.ShowError("Failed to delete search: ${e.message}")
                }
            }
        }
    }

    /**
     * PUBLIC API (Optional - for special cases)
     *
     * Fetches weather data based on current device location
     */
    fun fetchWeatherForCurrentLocation() {
        fetchWeatherForDefaultLocation()
    }
}