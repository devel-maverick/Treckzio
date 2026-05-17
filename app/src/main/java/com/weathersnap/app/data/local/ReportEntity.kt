package com.weathersnap.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reports")
data class ReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val observedAtIso: String,
    val notes: String,
    val imagePath: String,
    val originalSizeBytes: Long,
    val compressedSizeBytes: Long,
    val savedAtMillis: Long
)
