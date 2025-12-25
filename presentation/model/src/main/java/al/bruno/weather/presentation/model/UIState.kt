package al.bruno.weather.presentation.model

sealed class UIState {
    data object Success : UIState()
    data class Error(val error: String?) : UIState()
    data object Loading : UIState()
}