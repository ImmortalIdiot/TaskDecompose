package io.ii.domain.usecase

import io.ii.domain.model.Task
import io.ii.domain.repository.TaskRepository

/**
 * Юзкейс для получения конкретной задачи.
 *
 * Выполняет поиск задачи по её идентификатору.
 *
 * @property repository репозиторий задач
 */
class GetTaskUseCase(
    private val repository: TaskRepository
) : UseCase<String, Task?>() {

    override suspend fun execute(params: String): Task? = repository.getTaskById(params)
}
