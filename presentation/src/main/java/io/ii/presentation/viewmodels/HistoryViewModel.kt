package io.ii.presentation.viewmodels

import androidx.lifecycle.ViewModel
import io.ii.domain.usecase.LoadDecompositionHistoryUseCase
import io.ii.presentation.core.launchSafe
import io.ii.presentation.states.HistoryDateGroupUiState
import io.ii.presentation.states.HistoryUiState
import io.ii.presentation.utils.toEditorItemUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

internal class HistoryViewModel(
    private val loadDecompositionHistoryUseCase: LoadDecompositionHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        launchSafe(
            start = {
                _uiState.update { state ->
                    state.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                }
            },
            onError = { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage.orEmpty()
                    )
                }
            }
        ) {
            loadDecompositionHistoryUseCase()
                .map { tasks ->
                    tasks
                        .sortedByDescending { task -> task.createdAt }
                        .groupBy { task -> task.createdAt.toLocalDate() }
                        .toSortedMap(compareByDescending { it })
                        .map { (date, tasks) ->
                            HistoryDateGroupUiState(
                                date = date.format(DATE_FORMATTER),
                                tasks = tasks.map { task -> task.toEditorItemUiState() }
                            )
                        }
                }
                .collect { groups ->
                    _uiState.update { state ->
                        state.copy(
                            groups = groups,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    private fun Long.toLocalDate() =
        Instant.ofEpochMilli(this)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    }
}
