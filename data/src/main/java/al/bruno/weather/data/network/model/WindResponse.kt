package al.bruno.weather.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WindResponse(
    @SerialName("speed")
    val speed: Double,

    @SerialName("deg")
    val deg: Int,

    @SerialName("gust")
    val gust: Double? = null
)