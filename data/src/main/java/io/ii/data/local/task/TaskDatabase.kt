package io.ii.data.local.task

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.ii.data.local.task.dao.TaskDao
import io.ii.data.local.task.entity.TaskEntity


/**
 * База данных Room для хранения задач.
 */
@Database(
    entities = [TaskEntity::class],
    version = 2
)
internal abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao

    companion object {
        const val DATABASE_NAME = "task_db"

        /**
         * Добавляет название модели, использованной для декомпозиции.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN llm_model_name TEXT")
            }
        }
    }
}
