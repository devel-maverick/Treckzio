package com.weathersnap.app.data.remote

import com.weathersnap.app.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

// Geocoding API
interface GeocodingApi {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 8,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponseDto
}
