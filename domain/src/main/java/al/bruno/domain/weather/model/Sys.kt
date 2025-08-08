package al.bruno.domain.weather.model

import java.time.LocalDateTime

data class Sys(
    val id: Int? = null,
    val type: Int? = null,
    val country: String? = null,
    val sunrise: LocalDateTime? = null,
    val sunset: LocalDateTime? = null
)