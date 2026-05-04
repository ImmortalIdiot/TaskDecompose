package io.ii.data.repository

import androidx.room.withTransaction
import io.ii.data.local.TaskDatabase
import io.ii.data.local.dao.TaskDao
import io.ii.data.mapper.toEntities
import io.ii.data.mapper.toModel
import io.ii.data.mapper.toModelTree
import io.ii.data.remote.api.GigaChatApi
import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task
import io.ii.domain.repository.TaskRepository

internal class TaskRepositoryImpl(
    private val dao: TaskDao,
    private val db: TaskDatabase,
    private val api: GigaChatApi
) : TaskRepository {

    override suspend fun decomposeTask(
        taskTitle: String,
        taskDescription: String?,
        params: DecompositionParams
    ): Task {
        val newTask = TaskCreator.createTask(title = taskTitle, description = taskDescription)
        val prompt = PromptBuilder.build(task = newTask, params = params)

        return api.decomposeTask(prompt).toModel(newTask)
    }

    override suspend fun loadDecompositionHistory(): List<Task> {
        return dao.getAllTasks().toModelTree()
    }

    override suspend fun getTaskById(id: String): Task? {
        return dao.getTaskTreeById(id).toModel(id)
    }

    override suspend fun updateTask(task: Task) {
        val entities = task.toEntities()

        db.withTransaction {
            dao.deleteTaskById(task.id)
            dao.upsertAll(entities)
        }
    }

    override suspend fun deleteTask(id: String) {
        dao.deleteTaskById(id)
    }
}
