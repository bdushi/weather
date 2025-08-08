package al.bruno.domain.weather.model

data class Forecast(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val weather: List<ForecastWeather>,
    val city: City
)