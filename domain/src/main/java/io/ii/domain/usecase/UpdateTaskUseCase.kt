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
) : UseCase<UpdateTaskUseCase.Params, Unit>() {

    override suspend fun execute(params: Params) {
        repository.updateTask(
            task = params.task,
            llmModelName = params.llmModelName
        )
    }

    /**
     * Параметры сохранения задачи.
     *
     * @property task задача для сохранения
     * @property llmModelName название модели, использованной для декомпозиции
     */
    data class Params(
        val task: Task,
        val llmModelName: String?
    )
}
