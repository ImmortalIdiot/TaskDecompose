package io.ii.domain.usecase

import io.ii.domain.repository.TaskRepository

/**
 * Юзкейс для удаления задачи.
 *
 * Выполняет удаление задачи по её идентификатору.
 *
 * @property repository репозиторий задач
 */
class DeleteTaskUseCase(
    private val repository: TaskRepository
) : UseCase<String, Unit>() {

    override suspend fun execute(params: String) = repository.deleteTask(params)
}
