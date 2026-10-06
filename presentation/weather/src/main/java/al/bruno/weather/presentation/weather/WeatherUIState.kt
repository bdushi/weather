package al.bruno.weather.presentation.weather

import al.bruno.weather.core.viewmodel.UIState
import al.bruno.weather.presentation.model.CacheSearchUiModel
import al.bruno.weather.presentation.model.ForecastUiModel
import al.bruno.weather.presentation.model.LoadState
import al.bruno.weather.presentation.model.WeatherUiModel

data class WeatherUIState(
    val query: String = DEFAULT_QUERY,
    val weatherUiModel: WeatherUiModel? = null,
    val forecastUiModel: ForecastUiModel? = null,
    val loadState: LoadState = LoadState.Loading,
    val cacheSearch: List<CacheSearchUiModel> = emptyList(),
    // Reducer bookkeeping, not rendered
    /** Id of the latest fetch; results for any other id are stale and ignored. */
    val requestId: Long = 0,
    /** Query to save to history once the current fetch succeeds. */
    val pendingSave: String? = null,
) : UIState

const val DEFAULT_QUERY = "London"
