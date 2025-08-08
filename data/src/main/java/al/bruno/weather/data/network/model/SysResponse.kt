package al.bruno.weather.data.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class SysResponse(
    @SerialName("type")
    val type: Int? = null,

    @SerialName("id")
    val id: Int? = null,

    @SerialName("country")
    val country: String,

    @SerialName("sunrise")
    @Contextual
    val sunrise: LocalDateTime,

    @SerialName("sunset")
    @Contextual
    val sunset: LocalDateTime
)