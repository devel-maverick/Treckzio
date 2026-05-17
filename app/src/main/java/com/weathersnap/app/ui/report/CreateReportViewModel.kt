package com.weathersnap.app.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.data.repository.ReportDraftHolder
import com.weathersnap.app.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateReportViewModel @Inject constructor(
    private val draftHolder: ReportDraftHolder,
    private val repository: ReportRepository
) : ViewModel() {

    // Load initial values from the draft holder
    private val _state = MutableStateFlow(
        CreateReportUiState(
            snapshot = draftHolder.snapshot.value,
            capture = draftHolder.capture.value
        )
    )
    val state: StateFlow<CreateReportUiState> = _state.asStateFlow()

    init {
        combine(draftHolder.snapshot, draftHolder.capture) { snap, cap -> snap to cap }
            .onEach { (snap, cap) ->
                _state.update { it.copy(snapshot = snap, capture = cap) }
            }
            .launchIn(viewModelScope)
    }

    fun onNotesChange(value: String) {
        _state.update { it.copy(notes = value) }
    }

    fun onSaveClicked(onSaved: (Long) -> Unit) {
        val current = _state.value
        val snapshot = current.snapshot ?: return
        val capture = current.capture ?: return
        if (current.save is SaveState.Saving) return

        _state.update { it.copy(save = SaveState.Saving) }
        viewModelScope.launch {
            runCatching {
                repository.saveReport(
                    snapshot = snapshot,
                    notes = current.notes.trim(),
                    imagePath = capture.absolutePath,
                    originalSizeBytes = capture.originalBytes,
                    compressedSizeBytes = capture.compressedBytes
                )
            }.onSuccess { id ->
                _state.update { it.copy(save = SaveState.Success(id)) }
                draftHolder.setCapture(null)
                onSaved(id)
            }.onFailure { e ->
                _state.update {
                    it.copy(save = SaveState.Error(e.message ?: "Could not save report"))
                }
            }
        }
    }
}
