package com.weathersnap.app.ui.weather

import com.weathersnap.app.domain.model.CitySuggestion
import com.weathersnap.app.domain.model.WeatherSnapshot

sealed interface SuggestionState {
    object Idle : SuggestionState
    object Loading : SuggestionState
    data class Success(val items: List<CitySuggestion>) : SuggestionState
    object Empty : SuggestionState
    data class Error(val message: String) : SuggestionState
}

sealed interface WeatherState {
    object Idle : WeatherState
    object Loading : WeatherState
    data class Success(val snapshot: WeatherSnapshot) : WeatherState
    data class Error(val message: String) : WeatherState
}

data class WeatherUiState(
    val query: String = "",
    val suggestions: SuggestionState = SuggestionState.Idle,
    val weather: WeatherState = WeatherState.Idle,
    val showSuggestions: Boolean = false
)
