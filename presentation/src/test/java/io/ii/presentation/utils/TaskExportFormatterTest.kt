package io.ii.presentation.utils

import io.ii.presentation.states.SubtaskState
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Проверяет текстовое и JSON-представление экспортируемой декомпозиции.
 */
class TaskExportFormatterTest {

    @Test
    fun `txt export includes task metadata and nested subtasks`() {
        val export = listOf(task()).formatForExport(TaskExportFormat.Txt)

        assertTrue(export.contains("Root task"))
        assertTrue(export.contains("Описание: Root description"))
        assertTrue(!export.contains("Статус:"))
        assertTrue(export.contains("Модель: GigaChat"))
        assertTrue(export.contains("- [ ] Child task"))
        assertTrue(export.contains("  - [x] Nested task"))
    }

    @Test
    fun `txt export marks completed root task only when completed`() {
        val export = listOf(task().copy(isCompleted = true))
            .formatForExport(TaskExportFormat.Txt)

        assertTrue(export.startsWith("[x] Root task"))
    }

    @Test
    fun `json export includes task and nested subtasks`() {
        val export = listOf(task()).formatForExport(TaskExportFormat.Json)

        assertTrue(export.contains("\"title\": \"Root task\""))
        assertTrue(export.contains("\"llmModelName\": \"GigaChat\""))
        assertTrue(export.contains("\"title\": \"Nested task\""))
        assertTrue(export.contains("\"isCompleted\": true"))
    }

    private fun task(): ExportableTask =
        ExportableTask(
            id = "root",
            title = "Root task",
            description = "Root description",
            createdAt = 1L,
            isCompleted = false,
            llmModelName = "GigaChat",
            subtasks = listOf(
                SubtaskState(
                    id = "child",
                    title = "Child task",
                    description = null,
                    createdAt = 2L,
                    subtasks = listOf(
                        SubtaskState(
                            id = "nested",
                            title = "Nested task",
                            description = null,
                            createdAt = 3L,
                            isCompleted = true
                        )
                    )
                )
            )
        )
}
