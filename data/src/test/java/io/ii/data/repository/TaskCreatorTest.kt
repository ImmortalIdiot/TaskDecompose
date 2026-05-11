package io.ii.data.repository

import io.ii.data.remote.dto.TaskDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

/**
 * Проверяет создание доменных моделей задач из пользовательского ввода и DTO.
 *
 * Эти тесты защищают генерацию идентификаторов, времени создания и рекурсивное построение подзадач.
 */
class TaskCreatorTest {

    /**
     * Проверяет, что новая задача получает идентификатор, дату создания и переданные пользовательские поля.
     */
    @Test
    fun `createTask generates domain task with id and creation time`() {
        val before = System.currentTimeMillis()

        val task = TaskCreator.createTask(
            title = "Title",
            description = "Description"
        )

        val after = System.currentTimeMillis()

        UUID.fromString(task.id)
        assertEquals("Title", task.title)
        assertEquals("Description", task.description)
        assertTrue(task.createdAt in before..after)
        assertEquals(emptyList<Any>(), task.subtasks)
    }

    /**
     * Проверяет, что DTO подзадачи рекурсивно превращается в доменную модель с отдельными идентификаторами для каждого узла.
     */
    @Test
    fun `createSubtask recursively maps dto and generates unique ids`() {
        val task = TaskCreator.createSubtask(
            TaskDto(
                title = "Child",
                subtasks = listOf(TaskDto(title = "Grandchild"))
            )
        )

        assertEquals("Child", task.title)
        assertNull(task.description)
        assertEquals("Grandchild", task.subtasks.single().title)
        assertNotEquals(task.id, task.subtasks.single().id)
    }
}
