package io.ii.domain.usecase

import io.ii.domain.model.Task
import io.ii.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

/**
 * Юзкейс для загрузки истории декомпозиции задач.
 *
 * Возвращает список ранее сохранённых задач.
 *
 * @property repository репозиторий задач
 */
class LoadDecompositionHistoryUseCase(
    private val repository: TaskRepository
) {

    operator fun invoke(): Flow<List<Task>> = repository.loadDecompositionHistory()
}
