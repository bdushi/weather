package al.bruno.domain.weather.model

data class Condition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
