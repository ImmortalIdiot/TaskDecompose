package io.ii.presentation.viewmodels

import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task
import io.ii.domain.model.TaskHistoryItem
import io.ii.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Тестовая реализация TaskRepository для проверки ViewModel без базы данных и сети.
 *
 * Хранит параметры вызовов и позволяет управлять ответами use case из тестов.
 */
class FakeTaskRepository : TaskRepository {

    var decomposedTask = Task(
        id = "1",
        title = "Decomposed Task",
        description = null,
        createdAt = 1L
    )
    var decomposeCall: DecomposeCall? = null
        private set

    var updatedTask: Task? = null
        private set

    var deletedTaskId: String? = null
        private set
    var deletedTaskIds: List<String> = emptyList()
        private set

    var tasksById: Map<String, Task> = emptyMap()
    var updatedTaskLlmModelName: String? = null
        private set

    private val history = MutableStateFlow<List<TaskHistoryItem>>(emptyList())

    override suspend fun decomposeTask(
        taskTitle: String,
        taskDescription: String?,
        params: DecompositionParams
    ): Task {
        decomposeCall = DecomposeCall(taskTitle, taskDescription, params)
        return decomposedTask
    }

    override fun loadDecompositionHistory(): Flow<List<TaskHistoryItem>> = history

    override suspend fun getTaskById(id: String): Task? = tasksById[id]

    override suspend fun updateTask(
        task: Task,
        llmModelName: String?
    ) {
        updatedTask = task
        updatedTaskLlmModelName = llmModelName
    }

    override suspend fun deleteTask(id: String) {
        deletedTaskId = id
        deletedTaskIds = deletedTaskIds + id
    }

    /**
     * Публикует новый список задач в поток истории.
     */
    fun emitHistory(tasks: List<Task>) {
        history.value = tasks.map { task ->
            TaskHistoryItem(
                task = task,
                llmModelName = null
            )
        }
    }

    /**
     * Публикует новый список элементов истории в поток истории.
     */
    fun emitHistoryItems(items: List<TaskHistoryItem>) {
        history.value = items
    }

    /**
     * Снимок параметров, с которыми ViewModel запросила декомпозицию задачи.
     */
    data class DecomposeCall(
        val taskTitle: String,
        val taskDescription: String?,
        val params: DecompositionParams
    )
}
