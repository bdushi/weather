package al.bruno.domain.weather.model

import java.time.LocalDateTime

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime
)
