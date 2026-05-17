package io.ii.presentation.states

import io.ii.domain.model.DecompositionParams
import kotlin.String

/**
 * UI state экрана создания и редактирования задачи.
 *
 * Используется для создания новой задачи и для редактирования уже существующей.
 *
 * Если [id] и [createdAt] равны null - экран находится в режиме создания новой задачи.
 *
 * После получения результата декомпозиции или открытия задачи из истории экран переходит в режим редактирования.
 *
 * @property id идентификатор задачи
 * @property title название задачи
 * @property description описание задачи
 * @property createdAt время создания задачи в формате timestamp
 * @property subtasks список вложенных подзадач
 * @property depth глубина декомпозиции
 * @property hasPriority учитывать ли приоритетность подзадач
 * @property isLoading выполняется ли запрос декомпозиции
 * @property errorMessage текст ошибки
 * @property successMessage текст с уведомлением об успешной операции
 */
internal data class TaskEditorUiState(
    val id: String? = null,
    val title: String = "",
    val description: String = "",
    val createdAt: Long? = null,
    val subtasks: List<SubtaskState> = emptyList(),

    val depth: Int = DEFAULT_DEPTH,
    val hasPriority: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
) {
    val isEditMode: Boolean
        get() = id != null

    val canDecompose: Boolean
        get() = title.isNotBlank() && !isLoading

    val canSave: Boolean
        get() = id != null && title.isNotBlank() && !isLoading

    fun toDecompositionParams(): DecompositionParams =
        DecompositionParams(
            depth = depth,
            hasPriority = hasPriority
        )

    /**
     * Очищает форму задачи, если та находится в режиме создания.
     *
     * @return очищенная форма в режиме создания или сама задача в режиме редактирования
     */
    fun clear(): TaskEditorUiState {
        return if (!isEditMode) {
            copy(
                title = "",
                description = "",
                depth = DEFAULT_DEPTH,
                hasPriority = false,
                errorMessage = null,
                successMessage = null
            )
        } else {
            this
        }
    }

    companion object {
        private const val DEFAULT_DEPTH = 2
    }
}
