package io.ii.domain.usecase

import io.ii.domain.model.Task
import io.ii.domain.repository.TaskRepository

/**
 * Юзкейс для загрузки истории декомпозиции задач.
 *
 * Возвращает список ранее сохранённых задач.
 *
 * @property repository репозиторий задач
 */
class LoadDecompositionHistoryUseCase(
    private val repository: TaskRepository
) : UseCase<Unit, List<Task>>() {

    override suspend fun execute(params: Unit): List<Task> = repository.loadDecompositionHistory()
}
