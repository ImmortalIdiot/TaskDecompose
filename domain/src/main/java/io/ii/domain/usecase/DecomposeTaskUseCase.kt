package io.ii.domain.usecase

import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task
import io.ii.domain.repository.TaskRepository

/**
 * Use case для декомпозиции задачи.
 *
 * Выполняет разбиение исходной задачи на подзадачи с использованием
 * заданных параметров декомпозиции. Делегирует выполнение операции
 * репозиторию.
 *
 * @property repository репозиторий задач
 */
class DecomposeTaskUseCase(
    private val repository: TaskRepository
) : UseCase<DecomposeTaskUseCase.Params, Task>() {

    /**
     * Выполняет декомпозицию задачи.
     *
     * @param params входные параметры, содержащие задачу и настройки декомпозиции
     * @return задача с заполненным списком подзадач
     */
    override suspend fun execute(params: Params): Task =
        repository.decomposeTask(
            task = params.task,
            params = params.decompositionParams
        )

    /**
     * Параметры декомпозиции задачи.
     *
     * @property task исходная задача
     * @property decompositionParams параметры декомпозиции
     */
    data class Params(
        val task: Task,
        val decompositionParams: DecompositionParams
    )
}
