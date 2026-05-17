package com.weathersnap.app.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.weathersnap.app.data.repository.ReportDraftHolder
import com.weathersnap.app.domain.model.WeatherSnapshot
import com.weathersnap.app.ui.components.BrandHeader
import com.weathersnap.app.ui.components.WeatherSummary
import com.weathersnap.app.ui.theme.CardSurface
import com.weathersnap.app.ui.theme.CompressedText
import com.weathersnap.app.ui.theme.CompressedTint
import com.weathersnap.app.ui.theme.ErrorText
import com.weathersnap.app.ui.theme.LimeAccentSoft
import com.weathersnap.app.ui.theme.OnLime
import com.weathersnap.app.ui.theme.PressureText
import com.weathersnap.app.ui.theme.PressureTint
import com.weathersnap.app.ui.theme.SurfaceVariant
import com.weathersnap.app.ui.theme.TextMuted
import com.weathersnap.app.ui.theme.TextPrimary
import com.weathersnap.app.ui.theme.TextSecondary
import java.io.File

@Composable
fun CreateReportScreen(
    onBack: () -> Unit,
    onOpenCamera: () -> Unit,
    onSaved: () -> Unit,
    viewModel: CreateReportViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Go back if no weather data
    LaunchedEffect(state.snapshot) {
        if (state.snapshot == null) onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BrandHeader(
            title = "Create Report",
            subtitle = "Capture, compress, annotate",
            actionLabel = "Back",
            onAction = onBack
        )

        state.snapshot?.let { snap ->
            WeatherCard(snap)
        }

        PhotoCard(
            capture = state.capture,
            onCapture = onOpenCamera
        )

        NotesCard(
            notes = state.notes,
            onNotesChange = viewModel::onNotesChange
        )

        SaveButton(
            state = state.save,
            enabled = state.canSave,
            onClick = { viewModel.onSaveClicked { onSaved() } }
        )

        if (state.save is SaveState.Error) {
            Text(
                text = (state.save as SaveState.Error).message,
                color = ErrorText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun WeatherCard(snapshot: WeatherSnapshot) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WeatherSummary(snapshot = snapshot)
    }
}

@Composable
private fun PhotoCard(
    capture: ReportDraftHolder.Capture?,
    onCapture: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (capture == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("Photo preview", color = TextSecondary)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(File(capture.absolutePath))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Captured photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 10f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    SizePill(
                        title = "Original",
                        value = formatBytes(capture.originalBytes),
                        tint = PressureTint,
                        valueColor = PressureText,
                        modifier = Modifier.weight(1f)
                    )
                    SizePill(
                        title = "Compressed",
                        value = formatBytes(capture.compressedBytes),
                        tint = CompressedTint,
                        valueColor = CompressedText,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Button(
            onClick = onCapture,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = LimeAccentSoft,
                contentColor = OnLime
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text(if (capture == null) "Capture Photo" else "Retake Photo")
        }
    }
}

@Composable
private fun SizePill(
    title: String,
    value: String,
    tint: Color,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(tint)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(title, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, color = valueColor)
    }
}

@Composable
private fun NotesCard(notes: String, onNotesChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Field Notes", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = { Text("Notes") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LimeAccentSoft,
                unfocusedBorderColor = TextMuted,
                cursorColor = LimeAccentSoft,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}

@Composable
private fun SaveButton(state: SaveState, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = LimeAccentSoft,
            contentColor = OnLime,
            disabledContainerColor = LimeAccentSoft.copy(alpha = 0.4f),
            disabledContentColor = OnLime.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(50)
    ) {
        if (state is SaveState.Saving) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                color = OnLime,
                modifier = Modifier.height(18.dp)
            )
            Spacer(Modifier.height(0.dp))
            Text("  Saving…")
        } else {
            Text("Save Report")
        }
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes >= 1_048_576 -> "%.1f MB".format(bytes / 1_048_576.0)
    bytes >= 1024 -> "${bytes / 1024} KB"
    else -> "$bytes B"
}
