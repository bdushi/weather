package al.bruno.weather.presentation.weather

import al.bruno.weather.core.viewmodel.UIEffect

sealed interface WeatherUIEffect : UIEffect {
    data class ShowToast(val message: String) : WeatherUIEffect
    data class ShowError(val message: String) : WeatherUIEffect
}
