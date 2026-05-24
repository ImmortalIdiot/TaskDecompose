package io.ii.presentation.utils

import io.ii.domain.model.Task
import io.ii.domain.model.TaskHistoryItem
import org.junit.jupiter.api.Assertions.assertFalse
import io.ii.presentation.states.SubtaskState
import io.ii.presentation.states.TaskEditorUiState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

/**
 * Проверяет преобразования между domain-моделью задачи и UI-состояниями presentation-слоя.
 *
 * Эти тесты защищают правила отображения описания, идентичности задачи и вложенных подзадач.
 */
class ModelMapperTest {

    /**
     * Проверяет, что domain-задача рекурсивно преобразуется в состояние редактора.
     */
    @Test
    fun `task maps to editor state recursively`() {
        val task = Task(
            id = "root",
            title = "Root",
            description = null,
            createdAt = 1L,
            subtasks = listOf(
                Task(
                    id = "child",
                    title = "Child",
                    description = "Child description",
                    createdAt = 2L
                )
            )
        )

        val state = task.toEditorUiState()

        assertEquals("root", state.id)
        assertEquals("", state.description)
        assertEquals("child", state.subtasks.single().id)
        assertEquals("Child description", state.subtasks.single().description)
    }

    /**
     * Проверяет, что UI-состояние без id или даты создания не считается сохранённой domain-задачей.
     */
    @Test
    fun `editor state without persisted identity does not map to domain`() {
        assertNull(TaskEditorUiState(title = "Task").toDomain())
        assertNull(TaskEditorUiState(id = "id", title = "Task").toDomain())
    }

    /**
     * Проверяет, что пустое описание становится null,
     * а вложенные подзадачи сохраняются при преобразовании в domain-модель.
     */
    @Test
    fun `editor state maps blank description to null and preserves subtasks`() {
        val state = TaskEditorUiState(
            id = "root",
            title = "Root",
            description = " ",
            createdAt = 1L,
            subtasks = listOf(
                SubtaskState(
                    id = "child",
                    title = "Child",
                    description = "Description",
                    createdAt = 2L
                )
            )
        )

        val task = state.toDomain()

        assertEquals("root", task?.id)
        assertNull(task?.description)
        assertEquals("child", task?.subtasks?.single()?.id)
    }

    /**
     * Проверяет, что метаданные истории попадают только в UI state карточки истории.
     */
    @Test
    fun `history item maps model name to history card state`() {
        val state = TaskHistoryItem(
            task = Task(
                id = "root",
                title = "Root",
                description = null,
                createdAt = 1L
            ),
            llmModelName = "Mistral"
        ).toHistoryTaskUiState()

        assertEquals("root", state.task.id)
        assertEquals("Mistral", state.llmModelName)
        assertFalse(state.task.title.isBlank())
    }
}
