package al.bruno.weather.presentation.model

data class MainUiModel(
    val temp: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val pressure: Int,
    val humidity: Int,
    val seaLevel: Int? = null,
    val groundLevel: Int? = null
)