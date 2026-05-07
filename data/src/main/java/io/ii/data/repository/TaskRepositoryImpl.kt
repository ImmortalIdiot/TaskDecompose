package io.ii.data.repository

import androidx.room.withTransaction
import io.ii.data.local.task.TaskDatabase
import io.ii.data.local.task.dao.TaskDao
import io.ii.data.local.token.AccessTokenStorage
import io.ii.data.mapper.toEntities
import io.ii.data.mapper.toModel
import io.ii.data.mapper.toModelTree
import io.ii.data.remote.api.GigaChatApi
import io.ii.data.remote.dto.GigaChatAccessToken
import io.ii.data.utils.Constants
import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task
import io.ii.domain.repository.TaskRepository

internal class TaskRepositoryImpl(
    private val dao: TaskDao,
    private val db: TaskDatabase,
    private val api: GigaChatApi,
    private val tokenStorage: AccessTokenStorage
) : TaskRepository {

    override suspend fun decomposeTask(
        taskTitle: String,
        taskDescription: String?,
        params: DecompositionParams
    ): Task {
        val newTask = TaskCreator.createTask(title = taskTitle, description = taskDescription)
        val prompt = PromptBuilder.build(task = newTask, params = params)

        val token = getValidAccessToken()

        return api.decomposeTask(
            token = token.accessToken,
            prompt = prompt
        ).toModel(newTask)
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

    private suspend fun authorize(): GigaChatAccessToken {
        val token = api.authorize()
        tokenStorage.saveToken(token)

        return token
    }

    private suspend fun getValidAccessToken(): GigaChatAccessToken {
        val savedToken = tokenStorage.getToken()

        return if (savedToken != null && savedToken.isValid()) {
            savedToken
        } else {
            authorize()
        }
    }

    private fun GigaChatAccessToken.isValid(): Boolean {
        return accessToken.isNotBlank() &&
                expiresAt > System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_SAFETY_TIMEOUT_MILLIS
    }
}
