package al.bruno.presentation.weather

import al.bruno.weather.presentation.model.CacheSearchUiModel

sealed class WeatherUIEvent {
    data class Search(val query: String) : WeatherUIEvent()
    data class OnQueryChange(val query: String) : WeatherUIEvent()
    data class OnSelectedItems(val query: String) : WeatherUIEvent()
    data object OnClear : WeatherUIEvent()
    data class OnDeleteCacheSearch(val cacheSearchUiModel: CacheSearchUiModel) : WeatherUIEvent()
    data object OnRetry : WeatherUIEvent()
}