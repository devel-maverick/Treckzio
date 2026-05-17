package com.weathersnap.app.ui.report

import com.weathersnap.app.data.repository.ReportDraftHolder
import com.weathersnap.app.domain.model.WeatherSnapshot

sealed interface SaveState {
    object Idle : SaveState
    object Saving : SaveState
    data class Success(val id: Long) : SaveState
    data class Error(val message: String) : SaveState
}

data class CreateReportUiState(
    val snapshot: WeatherSnapshot? = null,
    val capture: ReportDraftHolder.Capture? = null,
    val notes: String = "",
    val save: SaveState = SaveState.Idle
) {
    val canSave: Boolean get() = snapshot != null && capture != null && save !is SaveState.Saving
}
