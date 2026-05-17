package com.weathersnap.app.ui.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.weathersnap.app.data.local.ReportEntity
import com.weathersnap.app.data.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ReportsUiState(
    val loading: Boolean = true,
    val items: List<ReportEntity> = emptyList()
) {
    val isEmpty: Boolean get() = !loading && items.isEmpty()
}

@HiltViewModel
class ReportsViewModel @Inject constructor(
    repository: ReportRepository
) : ViewModel() {

    val state: StateFlow<ReportsUiState> = repository.observeReports()
        .map { items -> ReportsUiState(loading = false, items = items) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReportsUiState()
        )
}
