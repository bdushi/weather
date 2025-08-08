package al.bruno.weather.presentation.model

import java.time.LocalDateTime

data class SysUiModel(
    val id: Int? = null,
    val type: Int? = null,
    val country: String? = null,
    val sunrise: LocalDateTime? = null,
    val sunset: LocalDateTime? = null,
)