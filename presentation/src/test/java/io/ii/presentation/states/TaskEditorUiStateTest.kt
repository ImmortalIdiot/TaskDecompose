package io.ii.presentation.states

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Проверяет вычисляемые свойства и операции состояния редактора задачи.
 *
 * Эти тесты фиксируют правила доступности действий пользователя и сброса формы.
 */
class TaskEditorUiStateTest {

    /**
     * Проверяет, что декомпозиция доступна только при непустом названии и отсутствии загрузки.
     */
    @Test
    fun `canDecompose requires non blank title and no loading`() {
        assertFalse(TaskEditorUiState(title = "").canDecompose)
        assertFalse(TaskEditorUiState(title = "Task", isLoading = true).canDecompose)
        assertTrue(TaskEditorUiState(title = "Task").canDecompose)
    }

    /**
     * Проверяет, что сохранение доступно только для уже созданной задачи с заданным названием и без активной загрузки.
     */
    @Test
    fun `canSave requires existing task id non blank title and no loading`() {
        assertFalse(TaskEditorUiState(id = null, title = "Task").canSave)
        assertFalse(TaskEditorUiState(id = "id", title = " ").canSave)
        assertFalse(TaskEditorUiState(id = "id", title = "Task", isLoading = true).canSave)
        assertTrue(TaskEditorUiState(id = "id", title = "Task").canSave)
    }

    /**
     * Проверяет, что очистка сбрасывает только форму создания, но не изменяет уже загруженную задачу.
     */
    @Test
    fun `clear resets creation form but keeps edit form unchanged`() {
        val creationState = TaskEditorUiState(
            title = "Task",
            description = "Description",
            depth = 4,
            hasPriority = true,
            errorMessage = "Error",
            successMessage = "Success"
        )
        val editState = creationState.copy(id = "id", createdAt = 1L)

        assertEquals(TaskEditorUiState(), creationState.clear())
        assertEquals(editState, editState.clear())
    }

    /**
     * Проверяет преобразование UI-настроек редактора в параметры декомпозиции.
     */
    @Test
    fun `toDecompositionParams maps current controls`() {
        val params = TaskEditorUiState(
            depth = 5,
            hasPriority = true
        ).toDecompositionParams()

        assertEquals(5, params.depth)
        assertTrue(params.hasPriority)
        assertFalse(params.hasTimeEstimation)
    }
}
