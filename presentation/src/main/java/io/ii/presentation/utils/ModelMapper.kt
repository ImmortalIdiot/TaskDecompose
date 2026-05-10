package io.ii.presentation.utils

import io.ii.domain.model.Task
import io.ii.presentation.states.SubtaskState
import io.ii.presentation.states.TaskEditorUiState

/**
 * Преобразует доменную модель задачи в UI state экрана редактирования.
 *
 * Используется при открытии существующей задачи или после получения результата декомпозиции.
 *
 * @return UI state экрана редактирования задачи
 */
internal fun Task.toEditorUiState(): TaskEditorUiState =
    TaskEditorUiState(
        id = id,
        title = title,
        description = description.orEmpty(),
        createdAt = createdAt,
        subtasks = subtasks.map { it.toEditorItemUiState() }
    )

/**
 * Преобразует доменную модель задачи в UI state подзадачи.
 *
 * Выполняет рекурсивное преобразование вложенных подзадач.
 *
 * @return UI state подзадачи
 */
internal fun Task.toEditorItemUiState(): SubtaskState =
    SubtaskState(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        subtasks = subtasks.map { it.toEditorItemUiState() }
    )

/**
 * Преобразует UI state экрана редактирования в доменную модель задачи.
 *
 * Если задача ещё не была создана и не содержит идентификатор или дату создания, возвращает null.
 *
 * @return доменная модель задачи или null
 */
internal fun TaskEditorUiState.toDomain(): Task? {
    val taskId = id
    val taskCreatedAt = createdAt

    if (taskId == null || taskCreatedAt == null) {
        return null
    }

    return Task(
        id = taskId,
        title = title,
        description = description.takeIf { it.isNotBlank() },
        createdAt = taskCreatedAt,
        subtasks = subtasks.map { it.toDomain() }
    )
}

/**
 * Преобразует UI state подзадачи в доменную модель.
 *
 * Выполняет рекурсивное преобразование вложенных подзадач.
 *
 * @return доменная модель задачи
 */
internal fun SubtaskState.toDomain(): Task =
    Task(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        subtasks = subtasks.map { it.toDomain() }
    )
