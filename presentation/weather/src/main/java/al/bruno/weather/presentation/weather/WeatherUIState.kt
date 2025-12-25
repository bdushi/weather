package al.bruno.weather.presentation.weather

import al.bruno.weather.core.viewmodel.UiState
import al.bruno.weather.presentation.model.CacheSearchUiModel
import al.bruno.weather.presentation.model.ForecastUiModel
import al.bruno.weather.presentation.model.UIState
import al.bruno.weather.presentation.model.WeatherUiModel

data class WeatherUIState(
    val query: String = DEFAULT_QUERY,
    val weatherUiModel: WeatherUiModel? = null,
    val forecastUiModel: ForecastUiModel? = null,
    val isSearching: Boolean = false,
    val uIState: UIState = UIState.Loading,
    val input: List<String> = emptyList(),
    val cacheSearch: List<CacheSearchUiModel> = emptyList(),
    val searchItem: List<String> = emptyList()
) : UiState
const val DEFAULT_QUERY = "London"