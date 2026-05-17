package com.weathersnap.app.ui.camera

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.data.repository.ReportDraftHolder
import com.weathersnap.app.util.ImageCompressor
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

sealed interface CameraState {
    object Idle : CameraState
    object Capturing : CameraState
    object Compressing : CameraState
    data class Done(val capture: ReportDraftHolder.Capture) : CameraState
    data class Error(val message: String) : CameraState
}

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val compressor: ImageCompressor,
    private val draftHolder: ReportDraftHolder
) : ViewModel() {

    private val _state = MutableStateFlow<CameraState>(CameraState.Idle)
    val state: StateFlow<CameraState> = _state.asStateFlow()

    // Create a new file for the camera to save into
    fun newCaptureFile(): File {
        val dir = File(context.filesDir, "captures").apply { mkdirs() }
        return File(dir, "capture_${System.currentTimeMillis()}.jpg")
    }

    fun onCaptureStarted() {
        _state.update { CameraState.Capturing }
    }

    fun onCaptureFailed(message: String) {
        _state.update { CameraState.Error(message) }
    }

    fun onCaptureSaved(file: File, onComplete: () -> Unit) {
        _state.update { CameraState.Compressing }
        viewModelScope.launch {
            runCatching { compressor.compress(file) }
                .onSuccess { result ->
                    // Delete original file
                    runCatching { file.delete() }
                    val capture = ReportDraftHolder.Capture(
                        absolutePath = result.outputFile.absolutePath,
                        originalBytes = result.originalBytes,
                        compressedBytes = result.compressedBytes
                    )
                    draftHolder.setCapture(capture)
                    _state.update { CameraState.Done(capture) }
                    onComplete()
                }
                .onFailure { e ->
                    _state.update { CameraState.Error(e.message ?: "Image compression failed") }
                }
        }
    }
}
