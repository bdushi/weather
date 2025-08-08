package al.bruno.weather.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CoordResponse(
    @SerialName("lon")
    val lon: Double,

    @SerialName("lat")
    val lat: Double
)