package io.ii.presentation.states

/**
 * UI state элемента подзадачи.
 *
 * Используется для отображения и редактирования вложенных подзадач.
 *
 * @property id идентификатор задачи
 * @property title название задачи
 * @property description описание задачи
 * @property createdAt время создания задачи в формате timestamp
 * @property subtasks список вложенных подзадач
 * @property isCompleted завершена ли подзадача
 */
internal data class SubtaskState(
    val id: String,
    val title: String,
    val description: String?,
    val createdAt: Long,
    val subtasks: List<SubtaskState> = emptyList(),
    val isCompleted: Boolean = false
)
