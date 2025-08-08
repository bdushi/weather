package al.bruno.weather.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CloudsResponse(
    @SerialName("all")
    val all: Int
)