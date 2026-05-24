package io.ii.domain.usecase

import io.ii.domain.model.TaskHistoryItem
import io.ii.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

/**
 * Юзкейс для загрузки истории декомпозиции задач.
 *
 * Возвращает список ранее сохранённых задач с метаданными истории.
 *
 * @property repository репозиторий задач
 */
class LoadDecompositionHistoryUseCase(
    private val repository: TaskRepository
) {

    operator fun invoke(): Flow<List<TaskHistoryItem>> = repository.loadDecompositionHistory()
}
