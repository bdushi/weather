package al.bruno.weather.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MainResponse(
    @SerialName("temp")
    val temp: Double,

    @SerialName("feels_like")
    val feelsLike: Double,

    @SerialName("temp_min")
    val tempMin: Double,

    @SerialName("temp_max")
    val tempMax: Double,

    @SerialName("pressure")
    val pressure: Int,

    @SerialName("humidity")
    val humidity: Int,

    @SerialName("sea_level")
    val seaLevel: Int? = null,

    @SerialName("grnd_level")
    val groundLevel: Int? = null
)