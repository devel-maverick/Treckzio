package com.weathersnap.app.data.repository

import com.weathersnap.app.data.cache.SuggestionCache
import com.weathersnap.app.data.remote.GeocodingApi
import com.weathersnap.app.data.remote.WeatherApi
import com.weathersnap.app.domain.model.CitySuggestion
import com.weathersnap.app.domain.model.WeatherCodes
import com.weathersnap.app.domain.model.WeatherSnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi,
    private val cache: SuggestionCache,
    private val io: CoroutineDispatcher
) {

    // Get city suggestions for the query
    suspend fun suggestCities(query: String): List<CitySuggestion> = withContext(io) {
        val trimmed = query.trim()
        if (trimmed.length <= 2) return@withContext emptyList()
        cache.get(trimmed)?.let { return@withContext it }

        val response = geocodingApi.search(name = trimmed)
        val mapped = response.results.orEmpty().map { r ->
            CitySuggestion(
                id = r.id,
                name = r.name,
                country = r.country,
                admin1 = r.admin1,
                latitude = r.latitude,
                longitude = r.longitude
            )
        }
        cache.put(trimmed, mapped)
        mapped
    }

    // Fetch current weather for a city
    suspend fun currentWeather(city: CitySuggestion): WeatherSnapshot = withContext(io) {
        val response = weatherApi.current(latitude = city.latitude, longitude = city.longitude)
        val c = response.current
            ?: throw IllegalStateException("No current weather returned for ${city.displayName}")
        val code = c.weatherCode ?: -1
        WeatherSnapshot(
            cityName = city.shortLabel.substringBefore(",").ifBlank { city.name },
            country = city.country,
            latitude = response.latitude,
            longitude = response.longitude,
            temperatureC = c.temperature ?: 0.0,
            condition = WeatherCodes.describe(code),
            weatherCode = code,
            humidity = c.humidity ?: 0,
            windSpeedMs = c.windSpeed ?: 0.0,
            pressureHpa = c.pressure ?: 0.0,
            observedAtIso = c.time
        )
    }
}
