package com.weathersnap.app.domain.model

// City suggestion from geocoding API
data class CitySuggestion(
    val id: Long,
    val name: String,
    val country: String?,
    val admin1: String?,
    val latitude: Double,
    val longitude: Double
) {
    val displayName: String
        get() = buildString {
            append(name)
            if (!admin1.isNullOrBlank() && !admin1.equals(name, ignoreCase = true)) {
                append(", ").append(admin1)
            }
            if (!country.isNullOrBlank()) append(", ").append(country)
        }

    val shortLabel: String
        get() = listOfNotNull(name, country).joinToString(", ")
}
