package io.ii.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Базовый абстрактный класс для всех use case.
 *
 * Инкапсулирует логику выполнения операции и предоставляет единый интерфейс вызова через invoke.
 *
 * Все use case должны реализовать метод [execute], содержащий
 * бизнес-логику операции.
 *
 * @param Params тип входных параметров
 * @param Result тип результата выполнения
 * @property coroutineDispatcher диспетчер корутин, в котором будет выполняться операция
 */
abstract class UseCase<in Params, out Result> protected constructor(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    protected abstract suspend fun execute(params: Params): Result

    suspend operator fun invoke(params: Params): Result =
        withContext(coroutineDispatcher) {
            execute(params)
        }
}
