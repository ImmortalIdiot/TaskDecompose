package io.ii.domain.model

/**
 * Доменная модель задачи.
 *
 * Представляет собой основную сущность приложения, содержащую описание задачи и результат её декомпозиции в виде подзадач.
 *
 * Поддерживает иерархическую структуру: каждая задача может содержать список
 * подзадач, каждая из которых также может являться самостоятельной задачей.
 *
 * @property id уникальный идентификатор задачи
 * @property title текст задачи
 * @property description опциональное дополнительное описание задачи
 * @property createdAt время создания задачи (timestamp формат)
 * @property subtasks список подзадач, полученных в результате декомпозиции
 * @property isCompleted завершена ли задача
 */
data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val createdAt: Long,
    val subtasks: List<Task> = emptyList(),
    val isCompleted: Boolean = false
)
