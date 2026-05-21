package io.ii.data.mapper

import io.ii.data.local.task.entity.TaskEntity
import io.ii.data.remote.dto.common.TaskDto
import io.ii.domain.model.Task
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * Проверяет бизнес-правила преобразования задач между доменной моделью, сущностями БД и DTO.
 *
 * Тесты защищают корректность сохранения и восстановления древовидной структуры задач.
 */
class TaskMapperTest {

    /**
     * Проверяет, что дерево задач превращается в плоский список сущностей с корректными ссылками на родительские задачи.
     */
    @Test
    fun `toEntities flattens task tree and preserves parent ids`() {
        val task = Task(
            id = "root",
            title = "Root",
            description = "Description",
            createdAt = 1L,
            subtasks = listOf(
                Task(
                    id = "child",
                    title = "Child",
                    description = null,
                    createdAt = 2L,
                    subtasks = listOf(
                        Task(
                            id = "grandchild",
                            title = "Grandchild",
                            description = null,
                            createdAt = 3L
                        )
                    )
                )
            )
        )

        val entities = task.toEntities()

        assertEquals(
            listOf(
                TaskEntity("root", null, "Root", "Description", 1L),
                TaskEntity("child", "root", "Child", null, 2L),
                TaskEntity("grandchild", "child", "Grandchild", null, 3L)
            ),
            entities
        )
    }

    /**
     * Проверяет, что из плоского списка восстанавливаются только корневые задачи,
     * а вложенные элементы попадают в соответствующие поддеревья.
     */
    @Test
    fun `toModelTree restores only root tasks with nested children`() {
        val entities = listOf(
            TaskEntity("orphan", "missing", "Orphan", null, 9L),
            TaskEntity("child2", "root", "Child 2", null, 3L),
            TaskEntity("root", null, "Root", "Description", 1L),
            TaskEntity("child1", "root", "Child 1", null, 2L),
            TaskEntity("secondRoot", null, "Second root", null, 4L)
        )

        val tasks = entities.toModelTree()

        assertEquals(listOf("root", "secondRoot"), tasks.map { it.id })
        assertEquals(listOf("child2", "child1"), tasks.first().subtasks.map { it.id })
        assertEquals(emptyList<Task>(), tasks.last().subtasks)
    }

    /**
     * Проверяет восстановление конкретной задачи вместе с её вложенными подзадачами.
     */
    @Test
    fun `toModel returns selected subtree`() {
        val entities = listOf(
            TaskEntity("root", null, "Root", null, 1L),
            TaskEntity("child", "root", "Child", null, 2L),
            TaskEntity("other", null, "Other", null, 3L)
        )

        val task = entities.toModel("root")

        assertEquals("root", task?.id)
        assertEquals(listOf("child"), task?.subtasks?.map { it.id })
    }

    /**
     * Проверяет, что поиск отсутствующей задачи возвращает null вместо пустой или некорректной модели.
     */
    @Test
    fun `toModel returns null when task is absent`() {
        val entities = listOf(TaskEntity("root", null, "Root", null, 1L))

        assertNull(entities.toModel("missing"))
    }

    /**
     * Проверяет защиту от циклических связей в дереве задач.
     */
    @Test
    fun `toModel detects cycles in task tree`() {
        val entities = listOf(
            TaskEntity("root", "child", "Root", null, 1L),
            TaskEntity("child", "root", "Child", null, 2L)
        )

        assertThrows(IllegalStateException::class.java) {
            entities.toModel("root")
        }
    }

    /**
     * Проверяет, что ответ API с подзадачами добавляется к исходной задаче, не изменяя заданные поля.
     */
    @Test
    fun `subtask DTO list becomes subtasks of original task`() {
        val originalTask = Task(
            id = "root",
            title = "Root",
            description = "Description",
            createdAt = 1L
        )
        val dto = listOf(
            TaskDto(
                title = "Child",
                subtasks = listOf(TaskDto(title = "Grandchild"))
            )
        )

        val task = dto.toModel(originalTask)

        assertEquals(originalTask.copy(subtasks = emptyList()), task.copy(subtasks = emptyList()))
        assertEquals("Child", task.subtasks.single().title)
        assertEquals("Grandchild", task.subtasks.single().subtasks.single().title)
    }
}
