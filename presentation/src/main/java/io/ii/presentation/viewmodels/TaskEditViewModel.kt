package io.ii.presentation.viewmodels

import androidx.lifecycle.ViewModel
import io.ii.domain.usecase.DecomposeTaskUseCase
import io.ii.domain.usecase.DeleteTaskUseCase
import io.ii.domain.usecase.UpdateTaskUseCase
import io.ii.presentation.R
import io.ii.presentation.core.ResourceProvider
import io.ii.presentation.core.launchSafe
import io.ii.presentation.states.TaskEditorItemUiState
import io.ii.presentation.states.TaskEditorUiState
import io.ii.presentation.utils.LoggingTags
import io.ii.presentation.utils.toDomain
import io.ii.presentation.utils.toEditorUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber

// TODO: add network check
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
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskEditorUiState())
    val uiState: StateFlow<TaskEditorUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { state ->
            state.copy(
                subtasks =
                    listOf(
                        TaskEditorItemUiState(
                            id = "1",
                            title = "Удалить ненужные программы",
                            description = null,
                            createdAt = 0L,
                            subtasks = listOf(
                                TaskEditorItemUiState(
                                    id = "1.1",
                                    title = "Открыть список установленных программ",
                                    description = null,
                                    createdAt = 0L
                                ),
                                TaskEditorItemUiState(
                                    id = "1.2",
                                    title = "Найти редко используемые приложения",
                                    description = null,
                                    createdAt = 0L
                                ),
                                TaskEditorItemUiState(
                                    id = "1.3",
                                    title = "Удалить ненужные программы",
                                    description = null,
                                    createdAt = 0L
                                )
                            )
                        ),
                        TaskEditorItemUiState(
                            id = "2",
                            title = "Очистить временные файлы",
                            description = null,
                            createdAt = 0L,
                            subtasks = listOf(
                                TaskEditorItemUiState(
                                    id = "2.1",
                                    title = "Очистить корзину",
                                    description = null,
                                    createdAt = 0L
                                ),
                                TaskEditorItemUiState(
                                    id = "2.2",
                                    title = "Удалить временные файлы системы",
                                    description = null,
                                    createdAt = 0L
                                ),
                                TaskEditorItemUiState(
                                    id = "2.3",
                                    title = "Очистить папку Downloads",
                                    description = null,
                                    createdAt = 0L,
                                    subtasks = listOf(
                                        TaskEditorItemUiState(
                                            id = "2.3.1",
                                            title = "Удалить старые архивы",
                                            description = null,
                                            createdAt = 0L
                                        ),
                                        TaskEditorItemUiState(
                                            id = "2.3.2",
                                            title = "Удалить дубликаты файлов",
                                            description = null,
                                            createdAt = 0L
                                        )
                                    )
                                )
                            )
                        ),
                        TaskEditorItemUiState(
                            id = "3",
                            title = "Проверить автозагрузку",
                            description = null,
                            createdAt = 0L,
                            subtasks = listOf(
                                TaskEditorItemUiState(
                                    id = "3.1",
                                    title = "Открыть диспетчер задач",
                                    description = null,
                                    createdAt = 0L
                                ),
                                TaskEditorItemUiState(
                                    id = "3.2",
                                    title = "Отключить лишние программы из автозагрузки",
                                    description = null,
                                    createdAt = 0L
                                )
                            )
                        ),
                        TaskEditorItemUiState(
                            id = "4",
                            title = "Проверить компьютер на вредоносное ПО",
                            description = null,
                            createdAt = 0L,
                            subtasks = listOf(
                                TaskEditorItemUiState(
                                    id = "4.1",
                                    title = "Обновить антивирусные базы",
                                    description = null,
                                    createdAt = 0L
                                ),
                                TaskEditorItemUiState(
                                    id = "4.2",
                                    title = "Запустить полное сканирование",
                                    description = null,
                                    createdAt = 0L
                                )
                            )
                        )
                    )
            )
        }
    }

    /**
     * Выполняет декомпозицию задачи.
     *
     * Отправляет введённые пользователем данные в use case,
     * получает декомпозированную задачу и обновляет состояние экрана.
     */
    fun decomposeTask() {
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

            _uiState.value = decomposedTask
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

            updateTaskUseCase(task)

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
                )
            } else {
                debug("Clear input form")

                _uiState.update { state -> state.clear() }
            }
        }
    }

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
