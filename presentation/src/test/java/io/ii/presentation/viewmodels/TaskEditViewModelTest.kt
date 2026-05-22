package io.ii.presentation.viewmodels

import io.ii.domain.model.LlmSettings
import io.ii.domain.model.Task
import io.ii.domain.usecase.DecomposeTaskUseCase
import io.ii.domain.usecase.DeleteTaskUseCase
import io.ii.domain.usecase.GetTaskUseCase
import io.ii.domain.usecase.ObserveLlmSettingsUseCase
import io.ii.domain.usecase.UpdateTaskUseCase
import io.ii.domain.repository.LlmSettingsRepository
import io.ii.presentation.R
import io.ii.presentation.core.NetworkProvider
import io.ii.presentation.core.ResourceProvider
import io.ii.presentation.states.TaskEditorUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

@OptIn(ExperimentalCoroutinesApi::class)
/**
 * Проверяет бизнес-логику редактора задачи на уровне ViewModel.
 *
 * Эти тесты фиксируют реакции на декомпозицию, отсутствие сети, сохранение и удаление задач.
 */
class TaskEditViewModelTest {

    @RegisterExtension
    @JvmField
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeTaskRepository()
    private val settingsRepository = FakeLlmSettingsRepository()
    private val networkProvider = FakeNetworkProvider()
    private val resourceProvider = FakeResourceProvider()

    private fun viewModel() = TaskEditViewModel(
        decomposeTaskUseCase = DecomposeTaskUseCase(repository),
        getTaskUseCase = GetTaskUseCase(repository),
        updateTaskUseCase = UpdateTaskUseCase(repository),
        deleteTaskUseCase = DeleteTaskUseCase(repository),
        observeLlmSettingsUseCase = ObserveLlmSettingsUseCase(settingsRepository),
        networkProvider = networkProvider,
        resourceProvider = resourceProvider
    )

    /**
     * Проверяет, что при отсутствии интернета декомпозиция не запускает use case
     * и показывает пользователю ошибку.
     */
    @Test
    fun `decomposeTask stops before use case when there is no internet`() {
        networkProvider.hasInternetConnection = false
        val viewModel = viewModel()

        viewModel.onTitleChange("Task")
        viewModel.decomposeTask()

        assertNull(repository.decomposeCall)
        assertEquals("No internet", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    /**
     * Проверяет, что декомпозиция передаёт в use case актуальные параметры,
     * применяет полученную задачу, сохраняет её и не сбрасывает выбранные параметры формы.
     */
    @Test
    fun `decomposeTask sends trimmed business params and keeps form params after result`() = runTest {
        repository.decomposedTask = Task(
            id = "TaskId",
            title = "Task",
            description = "Description",
            createdAt = 10L,
            subtasks = listOf(
                Task(
                    id = "SubtaskId",
                    title = "Subtask",
                    description = null,
                    createdAt = 11L
                )
            )
        )
        val viewModel = viewModel()

        viewModel.onTitleChange("Task")
        viewModel.onDescriptionChange("   ")
        viewModel.onDepthChange(4)
        viewModel.onPriorityChange(true)
        viewModel.decomposeTask()
        waitUntil {
            repository.decomposeCall != null &&
                viewModel.uiState.value.id == "TaskId" &&
                repository.updatedTask?.id == "TaskId"
        }

        assertEquals("Task", repository.decomposeCall?.taskTitle)
        assertNull(repository.decomposeCall?.taskDescription)
        assertEquals(4, repository.decomposeCall?.params?.depth)
        assertEquals(true, repository.decomposeCall?.params?.hasPriority)
        assertEquals("TaskId", viewModel.uiState.value.id)
        assertEquals("SubtaskId", viewModel.uiState.value.subtasks.single().id)
        assertEquals(4, viewModel.uiState.value.depth)
        assertEquals(true, viewModel.uiState.value.hasPriority)
        assertEquals("TaskId", repository.updatedTask?.id)
        assertEquals("SubtaskId", repository.updatedTask?.subtasks?.single()?.id)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    /**
     * Проверяет, что повторная декомпозиция уже полученной задачи сохраняет существующий корневой идентификатор.
     */
    @Test
    fun `decomposeTask saves regenerated result with current root id`() = runTest {
        val viewModel = viewModel()

        viewModel.onTitleChange("Task")
        repository.decomposedTask = Task(
            id = "FirstId",
            title = "Task",
            description = null,
            createdAt = 1L
        )
        viewModel.decomposeTask()
        waitUntil { viewModel.uiState.value.id == "FirstId" }

        repository.decomposedTask = Task(
            id = "SecondId",
            title = "Task",
            description = null,
            createdAt = 2L
        )
        viewModel.decomposeTask()
        waitUntil { viewModel.uiState.value.createdAt == 1L && !viewModel.uiState.value.isLoading }

        assertEquals("FirstId", viewModel.uiState.value.id)
        assertEquals(1L, viewModel.uiState.value.createdAt)
        assertEquals("FirstId", repository.updatedTask?.id)
        assertEquals(1L, repository.updatedTask?.createdAt)
    }

    /**
     * Проверяет, что задачу нельзя сохранить до декомпозиции или загрузки из истории.
     */
    @Test
    fun `saveTask reports error for task that has not been decomposed`() = runTest {
        val viewModel = viewModel()

        viewModel.onTitleChange("Task")
        viewModel.saveTask()
        waitUntil { viewModel.uiState.value.errorMessage == "Cannot save" }

        assertNull(repository.updatedTask)
        assertEquals("Cannot save", viewModel.uiState.value.errorMessage)
    }

    /**
     * Проверяет, что сохранение уже существующей задачи отправляет domain-модель в use case
     * и показывает сообщение об успехе.
     */
    @Test
    fun `saveTask updates persisted task and reports success`() = runTest {
        repository.tasksById = mapOf("TaskId" to persistedTask())
        val viewModel = viewModel()

        viewModel.loadTask("TaskId")
        waitUntil { viewModel.uiState.value.id == "TaskId" }
        viewModel.onDescriptionChange("")
        viewModel.saveTask()
        waitUntil { viewModel.uiState.value.successMessage == "Saved" }

        assertEquals("TaskId", repository.updatedTask?.id)
        assertNull(repository.updatedTask?.description)
        assertEquals("Saved", viewModel.uiState.value.successMessage)
    }

    /**
     * Проверяет, что удаление существующей задачи вызывает use case удаления
     * и возвращает редактор в начальное состояние с сообщением об успехе.
     */
    @Test
    fun `deleteTask removes persisted task and resets editor`() = runTest {
        repository.tasksById = mapOf("TaskId" to persistedTask())
        val viewModel = viewModel()

        viewModel.loadTask("TaskId")
        waitUntil { viewModel.uiState.value.id == "TaskId" }
        viewModel.deleteTask()
        waitUntil {
            repository.deletedTaskId != null &&
                viewModel.uiState.value.successMessage == "Deleted"
        }

        assertEquals("TaskId", repository.deletedTaskId)
        assertNull(viewModel.uiState.value.id)
        assertEquals("", viewModel.uiState.value.title)
        assertEquals("Deleted", viewModel.uiState.value.successMessage)
    }

    /**
     * Проверяет, что удаление черновика очищает форму без обращения к репозиторию.
     */
    @Test
    fun `deleteTask clears creation form without calling repository`() = runTest {
        val viewModel = viewModel()

        viewModel.onTitleChange("Draft")
        viewModel.onDescriptionChange("Description")
        viewModel.onDepthChange(5)
        viewModel.onPriorityChange(true)
        viewModel.deleteTask()

        assertNull(repository.deletedTaskId)
        assertEquals("", viewModel.uiState.value.title)
        assertEquals("", viewModel.uiState.value.description)
        assertEquals(2, viewModel.uiState.value.depth)
        assertEquals(false, viewModel.uiState.value.hasPriority)
    }

    private fun persistedTask() = Task(
        id = "TaskId",
        title = "Persisted",
        description = "Description",
        createdAt = 1L
    )

    /**
     * Дожидается выполнения асинхронной операции ViewModel до наступления ожидаемого состояния.
     */
    private suspend fun waitUntil(predicate: () -> Boolean) {
        withTimeout(1_000) {
            while (!predicate()) {
                delay(1)
            }
        }
    }

    /**
     * Тестовая реализация проверки сети с управляемым состоянием подключения.
     */
    private class FakeNetworkProvider : NetworkProvider {
        var hasInternetConnection = true

        override fun hasInternetConnection(): Boolean = hasInternetConnection
    }

    /**
     * Тестовая реализация ресурсов, возвращающая стабильные строки для проверок.
     */
    private class FakeResourceProvider : ResourceProvider {
        override fun getString(resId: Int): String =
            when (resId) {
                R.string.no_internet_connection_error -> "No internet"
                R.string.save_task_error -> "Cannot save"
                R.string.save_task_success -> "Saved"
                R.string.delete_task_success -> "Deleted"
                else -> error("Unknown string resource: $resId")
            }
    }

    private class FakeLlmSettingsRepository : LlmSettingsRepository {
        private val settings = MutableStateFlow(LlmSettings())

        override fun observeSettings(): Flow<LlmSettings> = settings

        override suspend fun getSettings(): LlmSettings = settings.value

        override suspend fun saveSettings(settings: LlmSettings) {
            this.settings.value = settings
        }
    }
}
