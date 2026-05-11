package io.ii.data.repository

import io.ii.data.remote.dto.TaskDto
import io.ii.domain.model.Task
import java.util.UUID

internal object TaskCreator {

    /**
     * Создаёт новую доменную модель задачи.
     *
     * @param title название задачи
     * @param description возможное описание задачи
     * @return созданная задача
     */
    fun createTask(
        title: String,
        description: String?
    ): Task {
        return Task(
            id = createTaskId(),
            title = title,
            description = description,
            createdAt = setCreationTimeMillis()
        )
    }

    /**
     * Создаёт доменную модель подзадачи на основе DTO.
     *
     * Рекурсивно преобразует вложенные подзадачи, формируя древовидную структуру.
     * Для каждой подзадачи генерируется новый идентификатор и устанавливается
     * текущее время создания.
     *
     * @param dto DTO подзадачи, полученной от API
     * @return доменная модель подзадачи
     */
    fun createSubtask(dto: TaskDto): Task {
        return Task(
            id = createTaskId(),
            title = dto.title,
            description = null,
            createdAt = setCreationTimeMillis(),
            subtasks = dto.subtasks.map(::createSubtask)
        )
    }

    /**
     * Генерирует идентификатор задачи в формате UUID.
     *
     * @return идентификатор задачи.
     */
    private fun createTaskId(): String = UUID.randomUUID().toString()

    /**
     * Задает время создания задачи.
     *
     * @return время создания задачи в формате timestamp.
     */
    private fun setCreationTimeMillis(): Long = System.currentTimeMillis()
}
