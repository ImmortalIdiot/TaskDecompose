package io.ii.domain.usecase

import io.ii.domain.model.Task
import io.ii.domain.repository.TaskRepository

/**
 * Юзкейс для обновления задачи.
 *
 * Сохраняет изменения существующей задачи.
 *
 * @property repository репозиторий задач
 */
class UpdateTaskUseCase(
    private val repository: TaskRepository
) : UseCase<Task, Unit>() {

    override suspend fun execute(params: Task): Unit = repository.updateTask(params)
}
