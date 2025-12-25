package al.bruno.weather.presentation.model

data class ForecastUiModel(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val weather: List<ForecastWeatherUiModel>,
    val city: CityUiModel
)