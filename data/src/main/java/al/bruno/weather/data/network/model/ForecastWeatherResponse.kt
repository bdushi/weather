package al.bruno.weather.data.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class ForecastWeatherResponse(
    @Contextual
    val dt: LocalDateTime,
    val main: MainResponse,
    val weather: List<ConditionResponse>,
    val clouds: CloudsResponse,
    val wind: WindResponse,
    val visibility: Int,
    val pop: Double,
    @SerialName("dt_txt")
    val dtTxt: String
)
