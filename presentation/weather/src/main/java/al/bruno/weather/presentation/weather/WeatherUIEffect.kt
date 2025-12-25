package al.bruno.weather.presentation.weather

import al.bruno.weather.core.viewmodel.UiEffect

sealed class WeatherUIEffect : UiEffect {
    data class ShowToast(val message: String) : WeatherUIEffect()
    data class ShowError(val error: String) : WeatherUIEffect()
}