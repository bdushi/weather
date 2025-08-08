package al.bruno.weather.data.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class WeatherResponse(
    @SerialName("coord")
    val coord: CoordResponse,

    @SerialName("weather")
    val weather: List<ConditionResponse>,

    @SerialName("base")
    val base: String,

    @SerialName("main")
    val main: MainResponse,

    @SerialName("visibility")
    val visibility: Int,

    @SerialName("wind")
    val wind: WindResponse,

    @SerialName("clouds")
    val clouds: CloudsResponse,

    @SerialName("dt")
    @Contextual
    val dt: LocalDateTime,

    @SerialName("sys")
    val sys: SysResponse,

    @SerialName("timezone")
    val timezone: Int,

    @SerialName("id")
    val id: Long,

    @SerialName("name")
    val name: String,

    @SerialName("cod")
    val cod: Int
)