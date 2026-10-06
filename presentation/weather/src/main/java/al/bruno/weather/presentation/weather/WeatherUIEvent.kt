package al.bruno.weather.presentation.weather

import al.bruno.weather.core.viewmodel.UIEvent
import al.bruno.weather.presentation.model.CacheSearchUiModel
import al.bruno.weather.presentation.model.ForecastUiModel
import al.bruno.weather.presentation.model.WeatherUiModel

sealed interface WeatherUIEvent : UIEvent {

    // ---- Intents: sent by the UI (Started is sent by the ViewModel's init) ----

    data object Started : WeatherUIEvent
    data class Search(val query: String) : WeatherUIEvent
    data class OnQueryChange(val query: String) : WeatherUIEvent
    data class OnSelectedItems(val query: String) : WeatherUIEvent
    data object OnClear : WeatherUIEvent
    data object OnRetry : WeatherUIEvent
    data class OnDeleteCacheSearch(val item: CacheSearchUiModel) : WeatherUIEvent

    // ---- Results: sent only by the executor (WeatherViewModel), never by the UI ----

    data class LocationResolved(val requestId: Long, val lat: Double, val lon: Double) : WeatherUIEvent
    data class WeatherLoaded(
        val requestId: Long,
        val cityName: String,
        val weather: WeatherUiModel,
        val forecast: ForecastUiModel,
    ) : WeatherUIEvent
    data class WeatherFailed(val requestId: Long, val message: String?) : WeatherUIEvent
    data class HistoryChanged(val items: List<CacheSearchUiModel>) : WeatherUIEvent
    data class HistoryFailed(val message: String?) : WeatherUIEvent
    data class DeleteFailed(val item: CacheSearchUiModel, val message: String?) : WeatherUIEvent
}
