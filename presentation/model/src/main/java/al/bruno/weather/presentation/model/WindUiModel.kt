package al.bruno.weather.presentation.model

data class WindUiModel(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)