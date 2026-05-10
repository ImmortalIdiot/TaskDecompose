package io.ii.presentation.states

/**
 * UI state экрана истории декомпозиции.
 *
 * @property groups задачи, сгруппированные по дате создания
 * @property isLoading загружается ли история
 * @property errorMessage текст ошибки
 */
internal data class HistoryUiState(
    val groups: List<HistoryDateGroupUiState> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

/**
 * Группа задач истории за один день.
 *
 * @property date отформатированная дата группы
 * @property tasks корневые задачи за эту дату
 */
internal data class HistoryDateGroupUiState(
    val date: String,
    val tasks: List<SubtaskState>
)
