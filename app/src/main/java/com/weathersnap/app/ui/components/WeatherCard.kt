package com.weathersnap.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.weathersnap.app.domain.model.WeatherSnapshot
import com.weathersnap.app.ui.theme.CompressedText
import com.weathersnap.app.ui.theme.HumidityText
import com.weathersnap.app.ui.theme.HumidityTint
import com.weathersnap.app.ui.theme.PressureText
import com.weathersnap.app.ui.theme.PressureTint
import com.weathersnap.app.ui.theme.TextPrimary
import com.weathersnap.app.ui.theme.TextSecondary
import com.weathersnap.app.ui.theme.WindText
import com.weathersnap.app.ui.theme.WindTint
import com.weathersnap.app.ui.theme.LimeAccentSoft
import kotlin.math.roundToInt

@Composable
fun WeatherSummary(snapshot: WeatherSnapshot, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = snapshot.shortLabel,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Text(
                    text = snapshot.condition,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
            TemperaturePill(temperatureC = snapshot.temperatureC)
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricChip(
                title = "Humidity",
                value = "${snapshot.humidity}%",
                tint = HumidityTint,
                valueColor = HumidityText,
                modifier = Modifier.weight(1f)
            )
            MetricChip(
                title = "Wind",
                value = "${formatNumber(snapshot.windSpeedMs, 2)} m/s",
                tint = WindTint,
                valueColor = WindText,
                modifier = Modifier.weight(1f)
            )
            MetricChip(
                title = "Pressure",
                value = formatNumber(snapshot.pressureHpa, 0),
                tint = PressureTint,
                valueColor = PressureText,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun TemperaturePill(temperatureC: Double) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LimeAccentSoft.copy(alpha = 0.18f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "${temperatureC.roundToInt()}°C",
            style = MaterialTheme.typography.headlineMedium,
            color = LimeAccentSoft
        )
    }
}

@Composable
fun MetricChip(
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
fun MetricChipCompressed(title: String, value: String, modifier: Modifier = Modifier) {
    MetricChip(
        title = title,
        value = value,
        tint = Color(0xFF193A36),
        valueColor = CompressedText,
        modifier = modifier
    )
}

private fun formatNumber(value: Double, decimals: Int): String =
    "%.${decimals}f".format(value)
