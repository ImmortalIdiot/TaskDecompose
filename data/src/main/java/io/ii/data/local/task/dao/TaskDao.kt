package io.ii.data.local.task.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.ii.data.local.task.entity.TaskEntity

/**
 * DAO-интерфейс для работы с задачами в базе данных.
 *
 * Предоставляет CRUD-операции для работы с задачами.
 */
@Dao
internal interface TaskDao {

    /**
     * Возвращает список всех задач из базы данных.
     *
     * @return список задач
     */
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<TaskEntity>

    /**
     * Возвращает задачу по её идентификатору со всеми подзадачами.
     *
     * @param id идентификатор задачи
     * @return задача или null, если задача не найдена
     */
    @Query(
        """
        WITH RECURSIVE task_tree AS (
        SELECT * FROM tasks WHERE id = :id
        UNION ALL
        SELECT child.* FROM tasks AS child
            INNER JOIN task_tree AS parent ON child.parent_id = parent.id
        )
        SELECT * FROM task_tree
    """
    )
    suspend fun getTaskTreeById(id: String): List<TaskEntity>

    /**
     * Сохраняет или обновляет список задач.
     *
     * При конфликте по первичному ключу существующая запись заменяется.
     *
     * @param tasks список сущностей задач
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tasks: List<TaskEntity>)

    /**
     * Удаляет задачу по её идентификатору.
     *
     * При наличии каскадного удаления в базе данных также удаляются подзадачи.
     *
     * @param id идентификатор задачи
     */
    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: String)
}
