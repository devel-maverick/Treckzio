package com.weathersnap.app.data.repository

import com.weathersnap.app.data.local.ReportDao
import com.weathersnap.app.data.local.ReportEntity
import com.weathersnap.app.domain.model.WeatherSnapshot
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportRepository @Inject constructor(
    private val dao: ReportDao,
    private val io: CoroutineDispatcher
) {

    fun observeReports(): Flow<List<ReportEntity>> = dao.observeAll().flowOn(io)

    fun observeReportCount(): Flow<Int> = dao.observeCount().flowOn(io)

    suspend fun saveReport(
        snapshot: WeatherSnapshot,
        notes: String,
        imagePath: String,
        originalSizeBytes: Long,
        compressedSizeBytes: Long
    ): Long = withContext(io) {
        val entity = ReportEntity(
            cityName = snapshot.cityName,
            country = snapshot.country,
            latitude = snapshot.latitude,
            longitude = snapshot.longitude,
            temperatureC = snapshot.temperatureC,
            condition = snapshot.condition,
            weatherCode = snapshot.weatherCode,
            humidity = snapshot.humidity,
            windSpeedMs = snapshot.windSpeedMs,
            pressureHpa = snapshot.pressureHpa,
            observedAtIso = snapshot.observedAtIso,
            notes = notes,
            imagePath = imagePath,
            originalSizeBytes = originalSizeBytes,
            compressedSizeBytes = compressedSizeBytes,
            savedAtMillis = System.currentTimeMillis()
        )
        dao.insert(entity)
    }
}
