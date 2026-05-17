package com.weathersnap.app.data.remote

import com.weathersnap.app.data.remote.dto.ForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

// Weather API
interface WeatherApi {
    @GET("v1/forecast")
    suspend fun current(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,pressure_msl",
        @Query("wind_speed_unit") windSpeedUnit: String = "ms",
        @Query("timezone") timezone: String = "auto"
    ): ForecastResponseDto
}
