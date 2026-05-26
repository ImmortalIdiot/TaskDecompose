package io.ii.presentation.utils

import io.ii.presentation.states.HistoryTaskUiState
import io.ii.presentation.states.SubtaskState
import io.ii.presentation.states.TaskEditorUiState
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

internal enum class TaskExportFormat(
    val extension: String,
    val mimeType: String
) {
    Txt(
        extension = "txt",
        mimeType = "text/plain"
    ),
    Json(
        extension = "json",
        mimeType = "application/json"
    )
}

internal data class ExportableTask(
    val id: String,
    val title: String,
    val description: String?,
    val createdAt: Long,
    val isCompleted: Boolean,
    val llmModelName: String?,
    val subtasks: List<SubtaskState>
)

internal fun TaskEditorUiState.toExportableTask(): ExportableTask? {
    val id = id ?: return null
    val createdAt = createdAt ?: return null

    return ExportableTask(
        id = id,
        title = title,
        description = description.takeIf { it.isNotBlank() },
        createdAt = createdAt,
        isCompleted = isCompleted,
        llmModelName = selectedLlmName.takeIf { it.isNotBlank() },
        subtasks = subtasks
    )
}

internal fun HistoryTaskUiState.toExportableTask(): ExportableTask =
    ExportableTask(
        id = task.id,
        title = task.title,
        description = task.description,
        createdAt = task.createdAt,
        isCompleted = task.isCompleted,
        llmModelName = llmModelName,
        subtasks = task.subtasks
    )

internal fun List<ExportableTask>.formatForExport(format: TaskExportFormat): String =
    when (format) {
        TaskExportFormat.Txt -> formatAsTxt()
        TaskExportFormat.Json -> formatAsJson()
    }

private fun List<ExportableTask>.formatAsTxt(): String =
    joinToString(separator = "\n\n") { task ->
        buildString {
            appendLine("${if (task.isCompleted) "[x] " else ""}${task.title}")

            if (!task.llmModelName.isNullOrBlank()) {
                appendLine("Модель: ${task.llmModelName}")
            }

            if (!task.description.isNullOrBlank()) {
                appendLine()
                appendLine("Описание: ${task.description}")
            }

            if (task.subtasks.isNotEmpty()) {
                appendLine()
                appendLine("Подзадачи:")
                task.subtasks.forEach { subtask ->
                    appendSubtask(subtask, level = 0)
                }
            }
        }.trimEnd()
    }

private fun StringBuilder.appendSubtask(
    subtask: SubtaskState,
    level: Int
) {
    val indent = "  ".repeat(level)
    val status = if (subtask.isCompleted) "[x]" else "[ ]"

    appendLine("$indent- $status ${subtask.title}")

    if (!subtask.description.isNullOrBlank()) {
        appendLine("$indent  ${subtask.description}")
    }

    subtask.subtasks.forEach { child ->
        appendSubtask(child, level + 1)
    }
}

private fun List<ExportableTask>.formatAsJson(): String =
    ExportJson.encodeToString(
        buildJsonArray {
            this@formatAsJson.forEach { task ->
                add(task.toJsonObject())
            }
        }
    )

private fun ExportableTask.toJsonObject(): JsonObject =
    buildJsonObject {
        put("id", id)
        put("title", title)
        put("description", description)
        put("createdAt", createdAt)
        put("isCompleted", isCompleted)
        put("llmModelName", llmModelName)
        putJsonArray("subtasks") {
            subtasks.forEach { subtask ->
                add(subtask.toJsonObject())
            }
        }
    }

private val ExportJson = Json {
    prettyPrint = true
}

private fun SubtaskState.toJsonObject(): JsonObject =
    buildJsonObject {
        put("id", id)
        put("title", title)
        put("description", description)
        put("createdAt", createdAt)
        put("isCompleted", isCompleted)
        putJsonArray("subtasks") {
            subtasks.forEach { subtask ->
                add(subtask.toJsonObject())
            }
        }
    }
