package com.weathersnap.app.data.repository

import com.weathersnap.app.domain.model.WeatherSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Holds the current draft report data shared between screens
@Singleton
class ReportDraftHolder @Inject constructor() {

    data class Capture(
        val absolutePath: String,
        val originalBytes: Long,
        val compressedBytes: Long
    )

    private val _snapshot = MutableStateFlow<WeatherSnapshot?>(null)
    val snapshot: StateFlow<WeatherSnapshot?> = _snapshot.asStateFlow()

    private val _capture = MutableStateFlow<Capture?>(null)
    val capture: StateFlow<Capture?> = _capture.asStateFlow()

    fun setSnapshot(value: WeatherSnapshot?) { _snapshot.value = value }
    fun setCapture(value: Capture?) { _capture.value = value }

    fun clearAll() {
        _snapshot.value = null
        _capture.value = null
    }
}
