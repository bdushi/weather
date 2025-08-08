package al.bruno.weather.presentation.model

import java.time.LocalDateTime

data class ForecastWeatherUiModel(
    val dt: LocalDateTime,
    val main: MainUiModel,
    val weather: List<ConditionUiModel>,
    val clouds: CloudsUiModel,
    val wind: WindUiModel,
    val visibility: Int,
    val pop: Int,
    val dtTxt: String
)
