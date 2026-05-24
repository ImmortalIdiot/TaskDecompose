package io.ii.data.repository

import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task
import io.ii.domain.model.TaskHistoryItem
import io.ii.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import kotlin.math.max

/**
 * In-memory репозиторий с mock-данными и mock-декомпозицией
 */
internal class MockTaskRepository : TaskRepository {

    private val mutex = Mutex()
    private var historyItems = createInitialTasks().map { task ->
        TaskHistoryItem(
            task = task,
            llmModelName = "GigaChat"
        )
    }
    private val historyFlow = MutableStateFlow(historyItems)

    override suspend fun decomposeTask(
        taskTitle: String,
        taskDescription: String?,
        params: DecompositionParams
    ): Task = mutex.withLock {
        val task = createTask(
            title = taskTitle,
            description = taskDescription,
            subtasks = createDecomposition(
                depth = params.depth,
                hasPriority = params.hasPriority
            )
        )

        task
    }

    override fun loadDecompositionHistory(): Flow<List<TaskHistoryItem>> = historyFlow.asStateFlow()

    override suspend fun getTaskById(id: String): Task? = mutex.withLock {
        historyItems.firstNotNullOfOrNull { item -> item.task.findById(id) }
    }

    override suspend fun updateTask(
        task: Task,
        llmModelName: String?
    ) {
        mutex.withLock {
            var updated = false

            historyItems = historyItems.map { item ->
                val root = item.task

                if (root.id == task.id) {
                    updated = true
                    item.copy(
                        task = task,
                        llmModelName = llmModelName
                    )
                } else {
                    val updatedRoot = root.replaceChild(task)
                    updated = updated || updatedRoot !== root
                    item.copy(task = updatedRoot)
                }
            }

            if (!updated) {
                historyItems = listOf(
                    TaskHistoryItem(
                        task = task,
                        llmModelName = llmModelName
                    )
                ) + historyItems
            }

            historyFlow.value = historyItems
        }
    }

    override suspend fun deleteTask(id: String) {
        mutex.withLock {
            historyItems = historyItems
                .filterNot { item -> item.task.id == id }
                .map { item -> item.copy(task = item.task.removeChild(id)) }

            historyFlow.value = historyItems
        }
    }

    private fun Task.findById(id: String): Task? {
        if (this.id == id) {
            return this
        }

        return subtasks.firstNotNullOfOrNull { task -> task.findById(id) }
    }

    private fun Task.replaceChild(task: Task): Task {
        if (subtasks.none { it.id == task.id || it.containsChild(task.id) }) {
            return this
        }

        return copy(
            subtasks = subtasks.map { child ->
                if (child.id == task.id) {
                    task
                } else {
                    child.replaceChild(task)
                }
            }
        )
    }

    private fun Task.removeChild(id: String): Task =
        copy(
            subtasks = subtasks
                .filterNot { task -> task.id == id }
                .map { task -> task.removeChild(id) }
        )

    private fun Task.containsChild(id: String): Boolean =
        subtasks.any { task -> task.id == id || task.containsChild(id) }

    private fun createDecomposition(
        depth: Int,
        hasPriority: Boolean
    ): List<Task> {
        val normalizedDepth = depth.coerceAtLeast(MIN_DECOMPOSITION_DEPTH)

        return createOkroshkaRecipe(normalizedDepth).mapIndexed { index, task ->
            createTask(
                title = if (hasPriority) {
                    "P${index + 1}. ${task.title}"
                } else {
                    task.title
                },
                subtasks = task.subtasks
            )
        }
    }

    private fun createOkroshkaRecipe(depth: Int): List<Task> {
        return listOf(
            createTask(
                title = "Подготовить ингредиенты",
                subtasks = createOkroshkaSubtasks(
                    depth = depth,
                    titles = listOf(
                        "Отварить картофель и яйца",
                        "Остудить отваренные продукты",
                        "Вымыть огурцы, редис и зелень"
                    )
                )
            ),
            createTask(
                title = "Нарезать основу",
                subtasks = createOkroshkaSubtasks(
                    depth = depth,
                    titles = listOf(
                        "Нарезать картофель и яйца кубиком",
                        "Добавить огурцы, редис и колбасу",
                        "Мелко порубить укроп и зеленый лук"
                    )
                )
            ),
            createTask(
                title = "Смешать и заправить",
                subtasks = createOkroshkaSubtasks(
                    depth = depth,
                    titles = listOf(
                        "Смешать нарезанные ингредиенты",
                        "Посолить и добавить сметану",
                        "Залить квасом или кефиром"
                    )
                )
            ),
            createTask(
                title = "Охладить и подать",
                subtasks = createOkroshkaSubtasks(
                    depth = depth,
                    titles = listOf(
                        "Убрать окрошку в холодильник на 20 минут",
                        "Перемешать перед подачей",
                        "Добавить горчицу или хрен по вкусу"
                    )
                )
            )
        )
    }

    private fun createOkroshkaSubtasks(
        depth: Int,
        titles: List<String>
    ): List<Task> {
        if (depth <= MIN_DECOMPOSITION_DEPTH) {
            return emptyList()
        }

        return titles.map { title ->
            createTask(
                title = title,
                subtasks = if (depth <= 2) {
                    emptyList()
                } else {
                    listOf(
                        createTask(title = "Подготовить рабочее место"),
                        createTask(title = "Проверить готовность шага")
                    )
                }
            )
        }
    }

    private companion object {
        private const val MIN_DECOMPOSITION_DEPTH = 1

        private fun createInitialTasks(): List<Task> {
            val now = System.currentTimeMillis()
            val day = 24 * 60 * 60 * 1000L

            return listOf(
                createTask(
                    title = "Подготовить презентацию проекта",
                    description = "Собрать материал, оформить структуру и проверить тайминг выступления.",
                    createdAt = now - day,
                    subtasks = listOf(
                        createTask(
                            title = "Собрать ключевые тезисы",
                            createdAt = now - day,
                            subtasks = listOf(
                                createTask(
                                    title = "Выписать цели проекта",
                                    createdAt = now - day
                                ),
                                createTask(
                                    title = "Добавить метрики результата",
                                    createdAt = now - day
                                )
                            )
                        ),
                        createTask(
                            title = "Оформить слайды",
                            createdAt = now - day
                        ),
                        createTask(
                            title = "Провести репетицию",
                            createdAt = now - day
                        )
                    )
                ),
                createTask(
                    title = "Навести порядок в задачах на неделю",
                    description = null,
                    createdAt = now - 2 * day,
                    subtasks = listOf(
                        createTask(
                            title = "Просмотреть backlog",
                            createdAt = now - 2 * day
                        ),
                        createTask(
                            title = "Выбрать приоритетные задачи",
                            createdAt = now - 2 * day
                        ),
                        createTask(
                            title = "Запланировать фокус-блоки",
                            createdAt = now - 2 * day
                        )
                    )
                )
            )
        }

        private fun createTask(
            title: String,
            description: String? = null,
            createdAt: Long = System.currentTimeMillis(),
            subtasks: List<Task> = emptyList()
        ): Task =
            Task(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                createdAt = max(createdAt, 0L),
                subtasks = subtasks
            )
    }
}
