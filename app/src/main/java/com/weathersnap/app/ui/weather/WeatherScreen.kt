package com.weathersnap.app.ui.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.weathersnap.app.domain.model.CitySuggestion
import com.weathersnap.app.domain.model.WeatherSnapshot
import com.weathersnap.app.ui.components.BrandHeader
import com.weathersnap.app.ui.components.WeatherSummary
import com.weathersnap.app.ui.theme.CardSurface
import com.weathersnap.app.ui.theme.ErrorText
import com.weathersnap.app.ui.theme.LimeAccent
import com.weathersnap.app.ui.theme.LimeAccentSoft
import com.weathersnap.app.ui.theme.OnLime
import com.weathersnap.app.ui.theme.Surface
import com.weathersnap.app.ui.theme.SurfaceVariant
import com.weathersnap.app.ui.theme.TextMuted
import com.weathersnap.app.ui.theme.TextPrimary
import com.weathersnap.app.ui.theme.TextSecondary

@Composable
fun WeatherScreen(
    onCreateReport: () -> Unit,
    onOpenReports: () -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BrandHeader(
            title = "WeatherSnap",
            subtitle = "Search a city to get weather",
            actionLabel = "Reports",
            onAction = onOpenReports
        )

        SearchCard(
            query = state.query,
            onQueryChange = viewModel::onQueryChange,
            onSearch = viewModel::onSearchClicked,
            suggestions = state.suggestions,
            showSuggestions = state.showSuggestions,
            onSuggestionSelected = viewModel::onSuggestionSelected,
            onDismissSuggestions = viewModel::dismissSuggestions
        )

        WeatherSection(
            state = state.weather,
            onCreateReport = onCreateReport
        )
    }
}

@Composable
private fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    suggestions: SuggestionState,
    showSuggestions: Boolean,
    onSuggestionSelected: (CitySuggestion) -> Unit,
    onDismissSuggestions: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                label = { Text("City") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LimeAccentSoft,
                    unfocusedBorderColor = TextMuted,
                    focusedLabelColor = LimeAccentSoft,
                    unfocusedLabelColor = TextSecondary,
                    cursorColor = LimeAccentSoft,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onSearch = { onSearch() }
                )
            )
            Button(
                onClick = onSearch,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LimeAccentSoft,
                    contentColor = OnLime
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("Search", style = MaterialTheme.typography.labelLarge)
            }
        }
        Text(
            text = "Enter more than 2 letters to start city suggestions.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )

        if (showSuggestions && suggestions !is SuggestionState.Idle) {
            SuggestionList(
                state = suggestions,
                onSelected = onSuggestionSelected,
                onDismiss = onDismissSuggestions
            )
        }
    }
}

@Composable
private fun SuggestionList(
    state: SuggestionState,
    onSelected: (CitySuggestion) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceVariant)
    ) {
        when (state) {
            SuggestionState.Idle -> Unit
            SuggestionState.Loading -> SuggestionMessage("Searching cities…")
            is SuggestionState.Error -> SuggestionMessage(state.message, color = ErrorText)
            SuggestionState.Empty -> SuggestionMessage(
                "No matches. Try a different spelling.",
                color = TextSecondary
            )
            is SuggestionState.Success -> LazyColumn(
                modifier = Modifier.heightIn(max = 240.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(state.items, key = { it.id }) { city ->
                    SuggestionRow(city = city, onClick = { onSelected(city) })
                    HorizontalDivider(color = TextMuted.copy(alpha = 0.15f))
                }
                item {
                    Text(
                        text = "Tap a city to load its weather",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDismiss() }
                            .padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestionRow(city: CitySuggestion, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(city.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Text(
            text = listOfNotNull(city.admin1, city.country).joinToString(", "),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun SuggestionMessage(text: String, color: androidx.compose.ui.graphics.Color = TextSecondary) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    )
}

@Composable
private fun WeatherSection(state: WeatherState, onCreateReport: () -> Unit) {
    when (state) {
        WeatherState.Idle -> WeatherPlaceholder()
        WeatherState.Loading -> WeatherLoading()
        is WeatherState.Error -> WeatherError(state.message)
        is WeatherState.Success -> WeatherSuccess(
            snapshot = state.snapshot,
            onCreateReport = onCreateReport
        )
    }
}

@Composable
private fun WeatherPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .padding(20.dp)
    ) {
        Text(
            "Search a city to see its live weather.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
private fun WeatherLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator(
            strokeWidth = 2.dp,
            color = LimeAccent,
            modifier = Modifier.height(20.dp)
        )
        Text("Loading current weather…", color = TextSecondary)
    }
}

@Composable
private fun WeatherError(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .padding(20.dp)
    ) {
        Text("Couldn't load weather", style = MaterialTheme.typography.titleMedium, color = ErrorText)
        Spacer(Modifier.height(4.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun WeatherSuccess(snapshot: WeatherSnapshot, onCreateReport: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardSurface)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        WeatherSummary(snapshot = snapshot)

        Button(
            onClick = onCreateReport,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = LimeAccentSoft,
                contentColor = OnLime
            ),
            shape = RoundedCornerShape(50)
        ) {
            Text("Create Report", style = MaterialTheme.typography.titleMedium)
        }
    }
}
