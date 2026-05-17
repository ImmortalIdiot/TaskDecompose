package io.ii.data.repository

import androidx.room.withTransaction
import io.ii.data.local.task.TaskDatabase
import io.ii.data.local.task.dao.TaskDao
import io.ii.data.local.token.AccessTokenStorage
import io.ii.data.mapper.toEntities
import io.ii.data.mapper.toModel
import io.ii.data.mapper.toModelTree
import io.ii.data.metrics.MetricsReporter
import io.ii.data.remote.api.GigaChatApi
import io.ii.data.remote.dto.GigaChatAccessToken
import io.ii.data.utils.Constants
import io.ii.data.utils.LoggingTags
import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task
import io.ii.domain.repository.TaskRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

internal class TaskRepositoryImpl(
    private val dao: TaskDao,
    private val db: TaskDatabase,
    private val api: GigaChatApi,
    private val tokenStorage: AccessTokenStorage,
    private val metricsReporter: MetricsReporter
) : TaskRepository {

    override suspend fun decomposeTask(
        taskTitle: String,
        taskDescription: String?,
        params: DecompositionParams
    ): Task {
        Timber.tag(LoggingTags.TASK_REPOSITORY).d("Start decompose in repo")
        val newTask = TaskCreator.createTask(title = taskTitle, description = taskDescription)
        val prompt = PromptBuilder.build(task = newTask, params = params)

        val token = getValidAccessToken()

        val result = decomposeTaskWithRetry(
            token = token.accessToken,
            prompt = prompt
        )
            .also { result ->
                Timber.tag(LoggingTags.TASK_REPOSITORY).d("Task decomposed in repo:\n${result.tasks}")
            }

        metricsReporter.reportTaskDecomposition(
            requestResponseDurationMillis = result.requestResponseDurationMillis,
            promptTokens = result.usage?.promptTokens,
            completionTokens = result.usage?.completionTokens,
            totalTokens = result.usage?.totalTokens
        )

        val tasks = result.tasks.toModel(newTask)

        return tasks
    }

    override fun loadDecompositionHistory(): Flow<List<Task>> {
        return dao.getAllTasks().map { tasks -> tasks.toModelTree() }
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

        Timber.tag(LoggingTags.TASK_REPOSITORY).d("Token saved")

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

    private suspend fun decomposeTaskWithRetry(
        token: String,
        prompt: String
    ) = retryOnDecompositionError {
        api.decomposeTask(
            token = token,
            prompt = prompt
        )
    }

    private suspend fun <T> retryOnDecompositionError(block: suspend () -> T): T {
        repeat(MAX_DECOMPOSITION_ATTEMPTS - 1) { attemptIndex ->
            try {
                return block()
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                Timber.tag(LoggingTags.TASK_REPOSITORY).w(
                    error,
                    "Decomposition attempt ${attemptIndex + 1}/$MAX_DECOMPOSITION_ATTEMPTS failed. Retrying."
                )
            }
        }

        return block()
    }

    private companion object {
        const val MAX_DECOMPOSITION_ATTEMPTS = 2
    }
}
