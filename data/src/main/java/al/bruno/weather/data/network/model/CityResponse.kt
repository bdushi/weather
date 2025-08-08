package al.bruno.weather.data.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class CityResponse(
    val id: Int,
    val name: String,
    val coord: CoordResponse,
    val country: String,
    val population: Int,
    val timezone: Int,
    @Contextual
    val sunrise: LocalDateTime,
    @Contextual
    val sunset: LocalDateTime
)