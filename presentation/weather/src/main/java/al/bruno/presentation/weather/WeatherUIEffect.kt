package al.bruno.presentation.weather

import al.bruno.presentation.ui.base.UiEffect

sealed class WeatherUIEffect : UiEffect {
    data class ShowToast(val message: String) : WeatherUIEffect()
    data class ShowError(val error: String) : WeatherUIEffect()
}