package al.bruno.weather.presentation.model

/** Loading status of a screen's main content. */
sealed interface LoadState {
    data object Loading : LoadState
    data object Success : LoadState
    data class Error(val message: String?) : LoadState
}
