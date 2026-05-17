package com.weathersnap.app.domain.model

// Weather data for a city
data class WeatherSnapshot(
    val cityName: String,
    val country: String?,
    val latitude: Double,
    val longitude: Double,
    val temperatureC: Double,
    val condition: String,
    val weatherCode: Int,
    val humidity: Int,
    val windSpeedMs: Double,
    val pressureHpa: Double,
    val observedAtIso: String
) {
    val shortLabel: String get() = listOfNotNull(cityName, country).joinToString(", ")
}

// Maps weather codes to descriptions
object WeatherCodes {
    fun describe(code: Int): String = when (code) {
        0 -> "Clear sky"
        1 -> "Mainly clear"
        2 -> "Partly cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing drizzle"
        61 -> "Slight rain"
        63 -> "Rain"
        65 -> "Heavy rain"
        66, 67 -> "Freezing rain"
        71 -> "Slight snow"
        73 -> "Snow"
        75 -> "Heavy snow"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers"
        85, 86 -> "Snow showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm with hail"
        else -> "Unknown"
    }
}
