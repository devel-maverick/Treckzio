package com.weathersnap.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.weathersnap.app.ui.theme.LimeAccentSoft
import com.weathersnap.app.ui.theme.OnLime

// Header used at the top of screens
@Composable
fun BrandHeader(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LimeAccentSoft)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = OnLime
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = OnLime.copy(alpha = 0.7f)
            )
        }
        if (actionLabel != null && onAction != null) {
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = OnLime,
                    contentColor = LimeAccentSoft
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(actionLabel)
            }
        }
    }
}
