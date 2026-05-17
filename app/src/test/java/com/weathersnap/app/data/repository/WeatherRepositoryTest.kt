package com.weathersnap.app.data.repository

import com.google.common.truth.Truth.assertThat
import com.weathersnap.app.data.cache.SuggestionCache
import com.weathersnap.app.data.remote.GeocodingApi
import com.weathersnap.app.data.remote.WeatherApi
import com.weathersnap.app.data.remote.dto.CurrentDto
import com.weathersnap.app.data.remote.dto.ForecastResponseDto
import com.weathersnap.app.data.remote.dto.GeoResultDto
import com.weathersnap.app.data.remote.dto.GeocodingResponseDto
import com.weathersnap.app.domain.model.CitySuggestion
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Test

class WeatherRepositoryTest {

    private val geocodingApi: GeocodingApi = mockk()
    private val weatherApi: WeatherApi = mockk()
    private val cache = SuggestionCache()
    private val repo = WeatherRepository(geocodingApi, weatherApi, cache, Dispatchers.Unconfined)

    @Test
    fun `short query returns empty list`() = runTest {
        val result = repo.suggestCities("ab")
        assertThat(result).isEmpty()
    }

    @Test
    fun `currentWeather returns correct snapshot`() = runTest {
        val city = CitySuggestion(1L, "Berlin", "Germany", "Berlin", 52.52, 13.41)
        val current = CurrentDto(
            time = "2026-05-12T10:00",
            temperature = 17.0,
            humidity = 43,
            weatherCode = 2,
            windSpeed = 3.83,
            pressure = 791.0
        )
        coEvery { weatherApi.current(city.latitude, city.longitude, any(), any(), any()) } returns
            ForecastResponseDto(city.latitude, city.longitude, current)

        val snapshot = repo.currentWeather(city)

        assertThat(snapshot.temperatureC).isEqualTo(17.0)
        assertThat(snapshot.condition).isEqualTo("Partly cloudy")
        assertThat(snapshot.humidity).isEqualTo(43)
    }
}
