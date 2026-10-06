package al.bruno.weather.presentation.weather

import al.bruno.weather.presentation.model.CacheSearchUiModel

/** Work the reducer asks the executor (WeatherViewModel) to do. Never seen by the UI. */
sealed interface WeatherCommand {
    data object ObserveHistory : WeatherCommand
    data class ResolveLocation(val requestId: Long) : WeatherCommand
    data class FetchWeather(val requestId: Long, val target: WeatherTarget) : WeatherCommand
    data class SaveQuery(val query: String) : WeatherCommand
    data class DeleteQuery(val item: CacheSearchUiModel) : WeatherCommand
}

sealed interface WeatherTarget {
    data class City(val name: String) : WeatherTarget
    data class Coordinates(val lat: Double, val lon: Double) : WeatherTarget
}
