package al.bruno.domain.weather.model

import java.time.LocalDateTime

data class ForecastWeather(
    val dt: LocalDateTime,
    val main: Main,
    val weather: List<Condition>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Int,
    val dtTxt: String
)
