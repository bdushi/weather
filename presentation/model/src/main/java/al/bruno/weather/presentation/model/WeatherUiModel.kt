package al.bruno.weather.presentation.model

import java.time.LocalDateTime

data class WeatherUiModel(
    val coord: CoordUiModel,
    val weather: List<ConditionUiModel>,
    val base: String,
    val main: MainUiModel,
    val visibility: Int,
    val wind: WindUiModel,
    val clouds: CloudsUiModel,
    val dt: LocalDateTime,
    val sys: SysUiModel,
    val timezone: Int,
    val id: Long,
    val name: String,
    val cod: Int
)