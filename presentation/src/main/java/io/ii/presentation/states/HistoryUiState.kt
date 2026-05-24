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
    val selectedTaskIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val taskIds: Set<String>
        get() = groups.flatMap { group -> group.tasks.map { item -> item.task.id } }.toSet()

    val hasTasks: Boolean
        get() = taskIds.isNotEmpty()

    val hasSelection: Boolean
        get() = selectedTaskIds.isNotEmpty()

    val isAllSelected: Boolean
        get() = hasTasks && selectedTaskIds.containsAll(taskIds)
}

/**
 * Группа задач истории за один день.
 *
 * @property date отформатированная дата группы
 * @property tasks корневые задачи за эту дату
 */
internal data class HistoryDateGroupUiState(
    val date: String,
    val tasks: List<HistoryTaskUiState>
)

/**
 * UI state карточки истории.
 *
 * @property task корневая задача с подзадачами
 * @property llmModelName название модели, использованной для декомпозиции
 */
internal data class HistoryTaskUiState(
    val task: SubtaskState,
    val llmModelName: String?
)
