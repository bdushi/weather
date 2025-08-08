package al.bruno.domain.weather.model

import java.time.LocalDateTime

data class Weather(
    val coord: Coord,
    val weather: List<Condition>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: LocalDateTime,
    val sys: Sys,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)