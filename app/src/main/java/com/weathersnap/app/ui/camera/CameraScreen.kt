package com.weathersnap.app.ui.camera

import android.Manifest
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.weathersnap.app.ui.theme.ErrorText
import com.weathersnap.app.ui.theme.LimeAccentSoft
import com.weathersnap.app.ui.theme.OnLime
import com.weathersnap.app.ui.theme.TextPrimary
import com.weathersnap.app.ui.theme.TextSecondary

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onCaptured: () -> Unit,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val cameraState by viewModel.state.collectAsStateWithLifecycle()
    val permission = rememberPermissionState(Manifest.permission.CAMERA)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (val status = permission.status) {
            is PermissionStatus.Denied -> PermissionGate(
                shouldShowRationale = status.shouldShowRationale,
                onRequest = { permission.launchPermissionRequest() },
                onClose = onClose
            )
            PermissionStatus.Granted -> CameraContent(
                state = cameraState,
                viewModel = viewModel,
                onClose = onClose,
                onCaptured = onCaptured
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionGate(
    shouldShowRationale: Boolean,
    onRequest: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera permission required",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        Text(
            text = if (shouldShowRationale)
                "Camera permission is needed to take photos for reports."
            else "Please grant camera access to continue.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
        Button(
            onClick = onRequest,
            colors = ButtonDefaults.buttonColors(
                containerColor = LimeAccentSoft,
                contentColor = OnLime
            ),
            shape = RoundedCornerShape(50)
        ) { Text("Grant permission") }
        OutlinedButton(
            onClick = onClose,
            modifier = Modifier.padding(top = 12.dp)
        ) { Text("Close", color = TextPrimary) }
    }
}

@Composable
private fun CameraContent(
    state: CameraState,
    viewModel: CameraViewModel,
    onClose: () -> Unit,
    onCaptured: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val imageCapture = remember { buildImageCapture() }
    val previewView = remember { PreviewView(context) }

    DisposableEffect(lifecycleOwner) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        val listener = Runnable {
            val provider = providerFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val selector = CameraSelector.DEFAULT_BACK_CAMERA
            runCatching {
                provider.unbindAll()
                provider.bindToLifecycle(lifecycleOwner, selector, preview, imageCapture)
            }.onFailure { viewModel.onCaptureFailed(it.message ?: "Camera bind failed") }
        }
        providerFuture.addListener(listener, ContextCompat.getMainExecutor(context))
        onDispose { runCatching { providerFuture.get().unbindAll() } }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Custom Camera",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            OutlinedButton(
                onClick = onClose,
                shape = RoundedCornerShape(50)
            ) {
                Text("Close", color = Color.White)
            }
        }

        // Bottom bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state is CameraState.Error) {
                Text(
                    text = state.message,
                    color = ErrorText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (state is CameraState.Compressing) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = LimeAccentSoft,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Compressing image…", color = Color.White)
                }
            }
            Button(
                onClick = {
                    val target = viewModel.newCaptureFile()
                    viewModel.onCaptureStarted()
                    val options = ImageCapture.OutputFileOptions.Builder(target).build()
                    imageCapture.takePicture(
                        options,
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageSavedCallback {
                            override fun onError(exception: ImageCaptureException) {
                                viewModel.onCaptureFailed(exception.message ?: "Capture failed")
                            }
                            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                                viewModel.onCaptureSaved(target, onComplete = onCaptured)
                            }
                        }
                    )
                },
                enabled = state !is CameraState.Capturing && state !is CameraState.Compressing,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LimeAccentSoft,
                    contentColor = OnLime
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Capture", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private fun buildImageCapture(): ImageCapture {
    return ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()
}
