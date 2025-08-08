package al.bruno.weather.presentation.model

import java.time.LocalDateTime

data class CityUiModel(
    val id: Int,
    val name: String,
    val coord: CoordUiModel,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: LocalDateTime,
    val sunset: LocalDateTime
)
