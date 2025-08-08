package al.bruno.weather.presentation.model

data class ConditionUiModel(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)
