package io.ii.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO задачи, полученной из ответа GigaChat.
 *
 * Используется рекурсивно: каждая задача может содержать список вложенных подзадач
 * того же формата.
 *
 * @property title название задачи
 * @property subtasks список вложенных подзадач
 */
@Serializable
internal data class TaskDto(
    val title: String,
    val subtasks: List<TaskDto> = emptyList()
)
