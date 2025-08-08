package al.bruno.weather.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<ForecastWeatherResponse>,
    val city: CityResponse
)