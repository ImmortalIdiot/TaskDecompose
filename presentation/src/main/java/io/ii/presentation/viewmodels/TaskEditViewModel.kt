package io.ii.presentation.viewmodels

import androidx.lifecycle.ViewModel
import io.ii.domain.model.LlmSettings
import io.ii.domain.usecase.DecomposeTaskUseCase
import io.ii.domain.usecase.DeleteTaskUseCase
import io.ii.domain.usecase.GetTaskUseCase
import io.ii.domain.usecase.ObserveLlmSettingsUseCase
import io.ii.domain.usecase.UpdateTaskUseCase
import io.ii.presentation.R
import io.ii.presentation.core.NetworkProvider
import io.ii.presentation.core.ResourceProvider
import io.ii.presentation.core.launchSafe
import io.ii.presentation.states.TaskEditorUiState
import io.ii.presentation.utils.LoggingTags
import io.ii.presentation.utils.toDomain
import io.ii.presentation.utils.toEditorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import timber.log.Timber

/**
 * ViewModel экрана создания и редактирования задачи.
 *
 * Отвечает за:
 * - декомпозицию задачи;
 * - сохранение изменений;
 * - удаление задачи;
 * - управление состоянием экрана редактора.
 */
internal class TaskEditViewModel(
    private val decomposeTaskUseCase: DecomposeTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val observeLlmSettingsUseCase: ObserveLlmSettingsUseCase,
    private val networkProvider: NetworkProvider,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskEditorUiState())
    val uiState: StateFlow<TaskEditorUiState> = _uiState.asStateFlow()

    private var loadedTaskId: String? = null
    private var llmSettings: LlmSettings = LlmSettings()

    init {
        launchSafe {
            observeLlmSettingsUseCase().collectLatest { settings ->
                llmSettings = settings
                _uiState.update { state ->
                    if (state.isEditMode && state.selectedLlmName.isNotBlank()) {
                        state
                    } else {
                        state.withLlmSettings(settings)
                    }
                }
            }
        }
    }

    /**
     * Переводит редактор в режим создания новой задачи.
     */
    fun createNewTask() {
        loadedTaskId = null
        _uiState.value = TaskEditorUiState().withLlmSettings(llmSettings)
    }

    /**
     * Загружает задачу для редактирования.
     *
     * Используется при переходе из истории на экран редактирования.
     *
     * @param taskId идентификатор задачи
     */
    fun loadTask(taskId: String) {
        if (loadedTaskId == taskId) {
            return
        }

        launchSafe(
            start = {
                _uiState.update { state ->
                    state.copy(
                        isLoading = true,
                        errorMessage = null,
                        successMessage = null
                    )
                }
            },
            onError = { error ->
                error(error.message ?: "Unknown")
                setError(error.localizedMessage.orEmpty())
            },
            final = {
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            }
        ) {
            val task = getTaskUseCase(taskId)

            if (task == null) {
                setError("Task not found")
                return@launchSafe
            }

            loadedTaskId = taskId
            _uiState.value = task.toEditorUiState().let { state ->
                if (state.selectedLlmName.isBlank()) {
                    state.withLlmSettings(llmSettings)
                } else {
                    state
                }
            }
        }
    }

    /**
     * Выполняет декомпозицию задачи.
     *
     * Отправляет введённые пользователем данные в use case,
     * получает декомпозированную задачу и обновляет состояние экрана.
     */
    fun decomposeTask() {
        if (!networkProvider.hasInternetConnection()) {
            setError(R.string.no_internet_connection_error)
            return
        }

        launchSafe(
            start = {
                debug("Start decompose.")

                _uiState.update { state ->
                    state.copy(
                        isLoading = true,
                        errorMessage = null,
                        successMessage = null
                    )
                }
            },
            onError = { error ->
                error(error.message ?: "Unknown")
                setError(error.localizedMessage.orEmpty())
            },
            final = {
                _uiState.update { state ->
                    state.copy(isLoading = false)
                }
            }
        ) {
            val state = _uiState.value

            debug("Params: title - \"${state.title}\"; description - \"${state.description}\"")
            val decomposedTask = decomposeTaskUseCase(
                DecomposeTaskUseCase.Params(
                    taskTitle = state.title,
                    taskDescription = state.description.takeIf { it.isNotBlank() },
                    decompositionParams = state.toDecompositionParams()
                )
            ).toEditorUiState()

            debug("End decomposition")

            val updatedTaskState = decomposedTask.copy(
                id = state.id ?: decomposedTask.id,
                createdAt = state.createdAt ?: decomposedTask.createdAt,
                depth = state.depth,
                hasPriority = state.hasPriority
            ).withLlmSettings(llmSettings)

            _uiState.value = updatedTaskState

            val task = updatedTaskState.toDomain()
            if (task == null) {
                setError(R.string.save_task_error)
                return@launchSafe
            }

            updateTaskUseCase(
                UpdateTaskUseCase.Params(
                    task = task,
                    llmModelName = updatedTaskState.selectedLlmName.takeIf { it.isNotBlank() }
                )
            )
        }
    }

    /**
     * Сохраняет текущую задачу
     */
    fun saveTask() {
        launchSafe(
            start = {
                debug("Start saving")

                _uiState.update { state ->
                    state.copy(
                        errorMessage = null,
                        successMessage = null
                    )
                }
            },
            onError = { error ->
                error(error.message ?: "Unknown")
                setError(error.localizedMessage.orEmpty())
            }
        ) {
            val task = _uiState.value.toDomain()

            if (task == null) {
                setError(R.string.save_task_error)
                return@launchSafe
            }

            updateTaskUseCase(
                UpdateTaskUseCase.Params(
                    task = task,
                    llmModelName = _uiState.value.selectedLlmName.takeIf { it.isNotBlank() }
                )
            )

            debug("Saved successfully")

            setSuccess(R.string.save_task_success)
        }
    }

    /**
     * Удаляет текущую задачу.
     *
     * Если задача ещё не была декомпозирована и не содержит идентификатор - очищает форму редактора.
     */
    fun deleteTask() {
        launchSafe(
            start = {
                debug("Start deleting")
                clearSuccessAndError()
            },
            onError = { error ->
                error(error.message ?: "Unknown")
                setError(error.localizedMessage.orEmpty())
            },
        ) {
            val deletingTask = _uiState.value

            if (deletingTask.isEditMode) {
                val taskId = deletingTask.id ?: return@launchSafe

                deleteTaskUseCase(taskId)

                debug("Deleted successfully")

                _uiState.value = TaskEditorUiState(
                    successMessage = resourceProvider.getString(R.string.delete_task_success)
                ).withLlmSettings(llmSettings)
            } else {
                debug("Clear input form")

                _uiState.update { state -> state.clear() }
            }
        }
    }

    private fun TaskEditorUiState.withLlmSettings(settings: LlmSettings): TaskEditorUiState =
        copy(
            selectedLlmName = if (settings.selectedModelId == LlmSettings.GIGACHAT_MODEL_ID) {
                settings.gigaChatModel
            } else {
                settings.customModels.firstOrNull { model ->
                    model.id == settings.selectedModelId
                }?.name.orEmpty()
            }
        )

    /**
     * Очищает сообщение об ошибке.
     */
    fun clearError() {
        _uiState.update { state ->
            state.copy(errorMessage = null)
        }
    }

    /**
     * Очищает сообщение об успешном выполнении операции.
     */
    fun clearSuccess() {
        _uiState.update { state ->
            state.copy(successMessage = null)
        }
    }

    /**
     * Очищает сообщение об успешном выполнении операции и сообщение об ошибке.
     */
    private fun clearSuccessAndError() {
        clearSuccess()
        clearError()
    }

    /**
     * Устанавливает сообщение об ошибке.
     *
     * @param message текст ошибки
     */
    private fun setError(message: String) {
        _uiState.update { state ->
            state.copy(errorMessage = message)
        }
    }

    /**
     * Устанавливает сообщение об ошибке из строкового ресурса.
     *
     * @param errorResId идентификатор строкового ресурса
     */
    private fun setError(errorResId: Int) {
        setError(resourceProvider.getString(errorResId))
    }

    /**
     * Устанавливает сообщение об успешном выполнении операции.
     *
     * @param successResId идентификатор строкового ресурса
     */
    private fun setSuccess(successResId: Int) {
        _uiState.update { state ->
            state.copy(
                successMessage = resourceProvider.getString(successResId)
            )
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { state -> state.copy(title = value) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { state -> state.copy(description = value) }
    }

    fun onDepthChange(value: Int) {
        _uiState.update { state -> state.copy(depth = value) }
    }

    fun onPriorityChange(value: Boolean) {
        _uiState.update { state -> state.copy(hasPriority = value) }
    }

    companion object {

        private fun debug(message: String) {
            Timber.tag(LoggingTags.TASK_EDIT_VM).d(message)
        }

        private fun error(message: String) {
            Timber.tag(LoggingTags.TASK_EDIT_VM).e(message)
        }
    }
}
