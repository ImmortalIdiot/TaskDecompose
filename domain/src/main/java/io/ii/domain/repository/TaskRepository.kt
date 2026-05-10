package io.ii.domain.repository

import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий задач.
 *
 * Определяет контракт для работы с задачами и их декомпозицией.
 * Реализация должна обеспечить получение данных из различных источников,
 * а также их сохранение.
 *
 * Является абстракцией над источниками данных и используется в слое domain.
 */
interface TaskRepository {

    /**
     * Выполняет декомпозицию задачи.
     *
     * На основе исходной задачи и заданных параметров формирует набор подзадач.
     *
     * @param taskTitle текст исходной задачи, введенный пользователем
     * @param taskDescription возможное описание задачи, введенное пользователем
     * @param params параметры декомпозиции
     * @return задача с заполненным списком подзадач
     */
    suspend fun decomposeTask(
        taskTitle: String,
        taskDescription: String?,
        params: DecompositionParams
    ): Task

    /**
     * Загружает историю декомпозированных задач.
     *
     * @return список ранее сохранённых задач
     */
    fun loadDecompositionHistory(): Flow<List<Task>>

    /**
     * Получает задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     * @return найденная задача или null, если задача не существует
     */
    suspend fun getTaskById(id: String): Task?

    /**
     * Сохраняет задачу.
     *
     * Используется для обновления существующей задачи.
     *
     * @param task задача для сохранения
     */
    suspend fun updateTask(task: Task)

    /**
     * Удаляет задачу по её идентификатору.
     *
     * @param id идентификатор задачи
     */
    suspend fun deleteTask(id: String)
}
