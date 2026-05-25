package io.ii.presentation.viewmodels

import androidx.lifecycle.ViewModel
import io.ii.domain.usecase.DeleteTaskUseCase
import io.ii.domain.usecase.LoadDecompositionHistoryUseCase
import io.ii.domain.usecase.UpdateTaskUseCase
import io.ii.presentation.core.launchSafe
import io.ii.presentation.states.HistoryDateGroupUiState
import io.ii.presentation.states.HistoryTaskUiState
import io.ii.presentation.states.HistoryUiState
import io.ii.presentation.utils.setAllCompleted
import io.ii.presentation.utils.toDomain
import io.ii.presentation.utils.toHistoryTaskUiState
import io.ii.presentation.utils.updateCompletedCascade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * ViewModel экрана истории декомпозированных задач.
 *
 * Отвечает за:
 * - загрузку истории задач;
 * - группировку истории по дате;
 */
internal class HistoryViewModel(
    private val loadDecompositionHistoryUseCase: LoadDecompositionHistoryUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val clock: Clock = Clock.systemDefaultZone()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState(isLoading = true))
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    /**
     * Загружает историю декомпозиций и группирует задачи по дате создания.
     */
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
                        .sortedByDescending { item -> item.task.createdAt }
                        .groupBy { item -> item.task.createdAt.toLocalDate() }
                        .toSortedMap(compareByDescending { it })
                        .map { (date, tasks) ->
                            HistoryDateGroupUiState(
                                date = date.formatForHistory(),
                                tasks = tasks.map { item -> item.toHistoryTaskUiState() }
                            )
                        }
                }
                .collect { groups ->
                    val taskIds = groups.flatMap { group -> group.tasks.map { item -> item.task.id } }.toSet()

                    _uiState.update { state ->
                        state.copy(
                            groups = groups,
                            selectedTaskIds = state.selectedTaskIds.intersect(taskIds),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    /**
     * Выбирает карточку истории или снимает выбор.
     */
    fun toggleTaskSelection(taskId: String) {
        _uiState.update { state ->
            val selectedTaskIds = if (taskId in state.selectedTaskIds) {
                state.selectedTaskIds - taskId
            } else {
                state.selectedTaskIds + taskId
            }

            state.copy(selectedTaskIds = selectedTaskIds)
        }
    }

    /**
     * Очищает выбранные элементы истории или всю историю, если выбор пустой.
     */
    fun deleteSelectedOrAllHistory() {
        launchSafe(
            onError = { error ->
                _uiState.update { state ->
                    state.copy(errorMessage = error.localizedMessage.orEmpty())
                }
            }
        ) {
            val state = _uiState.value
            val deletingIds = if (state.hasSelection) {
                state.selectedTaskIds
            } else {
                state.taskIds
            }

            deletingIds.forEach { taskId ->
                deleteTaskUseCase(taskId)
            }

            _uiState.update { currentState ->
                currentState.copy(selectedTaskIds = emptySet())
            }
        }
    }

    fun onRootCompletedChange(
        rootTaskId: String,
        isCompleted: Boolean
    ) {
        val task = findTask(rootTaskId) ?: return
        saveHistoryTask(
            task.copy(
                task = task.task.copy(
                    isCompleted = isCompleted,
                    subtasks = task.task.subtasks.setAllCompleted(isCompleted)
                )
            )
        )
    }

    fun onSubtaskCompletedChange(
        rootTaskId: String,
        subtaskId: String,
        isCompleted: Boolean
    ) {
        val task = findTask(rootTaskId) ?: return
        saveHistoryTask(
            task.copy(
                task = task.task.copy(
                    subtasks = task.task.subtasks.updateCompletedCascade(
                        id = subtaskId,
                        isCompleted = isCompleted
                    )
                )
            )
        )
    }

    private fun saveHistoryTask(task: HistoryTaskUiState) {
        launchSafe(
            onError = { error ->
                _uiState.update { state ->
                    state.copy(errorMessage = error.localizedMessage.orEmpty())
                }
            }
        ) {
            updateTaskUseCase(
                UpdateTaskUseCase.Params(
                    task = task.task.toDomain(),
                    llmModelName = task.llmModelName
                )
            )
        }
    }

    private fun findTask(taskId: String): HistoryTaskUiState? =
        _uiState.value.groups
            .flatMap { group -> group.tasks }
            .firstOrNull { task -> task.task.id == taskId }

    /**
     * Преобразует timestamp в локальную дату устройства.
     */
    private fun Long.toLocalDate() =
        Instant.ofEpochMilli(this)
            .atZone(clock.zone)
            .toLocalDate()

    private fun LocalDate.formatForHistory(): String {
        val today = LocalDate.now(clock)

        return when (this) {
            today -> "Сегодня"
            today.minusDays(1) -> "Вчера"
            else -> format(DATE_FORMATTER)
        }
    }

    private companion object {
        val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(
            "d MMMM yyyy",
            Locale.forLanguageTag("ru")
        )
    }
}
