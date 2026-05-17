package com.weathersnap.app.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.data.repository.ReportDraftHolder
import com.weathersnap.app.data.repository.WeatherRepository
import com.weathersnap.app.domain.model.CitySuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val draftHolder: ReportDraftHolder
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private val queryFlow = MutableStateFlow("")
    private var weatherJob: Job? = null

    init {
        queryFlow
            .debounce(300)
            .distinctUntilChanged()
            .filter { it.trim().length > 2 }
            .onEach { runSuggestions(it) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) {
        _state.update { it.copy(query = value, showSuggestions = true) }
        queryFlow.value = value
        if (value.trim().length <= 2) {
            _state.update { it.copy(suggestions = SuggestionState.Idle) }
        } else {
            _state.update { it.copy(suggestions = SuggestionState.Loading) }
        }
    }

    fun onSuggestionSelected(city: CitySuggestion) {
        _state.update {
            it.copy(
                query = city.shortLabel,
                showSuggestions = false,
                suggestions = SuggestionState.Idle,
                weather = WeatherState.Loading
            )
        }
        loadWeather(city)
    }

    fun onSearchClicked() {
        val current = _state.value
        // Pick first suggestion if available
        val first = (current.suggestions as? SuggestionState.Success)?.items?.firstOrNull()
        if (first != null) {
            onSuggestionSelected(first)
        } else if (current.query.trim().length > 2) {
            runSuggestions(current.query, autoPickFirst = true)
        }
    }

    fun dismissSuggestions() {
        _state.update { it.copy(showSuggestions = false) }
    }

    private fun runSuggestions(query: String, autoPickFirst: Boolean = false) {
        viewModelScope.launch {
            runCatching { repository.suggestCities(query) }
                .onSuccess { list ->
                    val next = when {
                        list.isEmpty() -> SuggestionState.Empty
                        else -> SuggestionState.Success(list)
                    }
                    _state.update { it.copy(suggestions = next, showSuggestions = true) }
                    if (autoPickFirst) list.firstOrNull()?.let(::onSuggestionSelected)
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(suggestions = SuggestionState.Error(e.message ?: "Search failed"))
                    }
                }
        }
    }

    private fun loadWeather(city: CitySuggestion) {
        weatherJob?.cancel()
        weatherJob = viewModelScope.launch {
            runCatching { repository.currentWeather(city) }
                .onSuccess { snapshot ->
                    draftHolder.setSnapshot(snapshot)
                    _state.update { it.copy(weather = WeatherState.Success(snapshot)) }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(weather = WeatherState.Error(e.message ?: "Failed to load weather"))
                    }
                }
        }
    }
}

