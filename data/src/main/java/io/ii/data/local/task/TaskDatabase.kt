package io.ii.data.local.task

import androidx.room.Database
import androidx.room.RoomDatabase
import io.ii.data.local.task.dao.TaskDao
import io.ii.data.local.task.entity.TaskEntity


/**
 * База данных Room для хранения задач.
 */
@Database(
    entities = [TaskEntity::class],
    version = 1
)
internal abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao

    companion object {
        const val DATABASE_NAME = "task_db"
    }
}
