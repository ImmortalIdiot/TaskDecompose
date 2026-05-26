package io.ii.presentation.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal data class TaskExportResult(
    val fileName: String,
    val displayPath: String,
    val uri: Uri
)

internal fun Context.saveTasksExport(
    tasks: List<ExportableTask>,
    format: TaskExportFormat,
    clock: Clock = Clock.systemDefaultZone()
): TaskExportResult {
    val fileName = createExportFileName(format, clock)
    val displayPath = "${Environment.DIRECTORY_DOWNLOADS}/$EXPORT_DIRECTORY/$fileName"
    val content = tasks.formatForExport(format)
    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, format.mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/$EXPORT_DIRECTORY")
    }

    val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
        ?: error("Cannot create export file")

    try {
        contentResolver.openOutputStream(uri)?.use { output ->
            output.write(content.toByteArray(StandardCharsets.UTF_8))
        } ?: error("Cannot open export file")
    } catch (error: Throwable) {
        contentResolver.delete(uri, null, null)
        throw error
    }

    return TaskExportResult(
        fileName = fileName,
        displayPath = displayPath,
        uri = uri
    )
}

internal fun Context.openExportedTask(result: TaskExportResult) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(result.uri, result.fileName.mimeTypeFromName())
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    startActivity(Intent.createChooser(intent, result.displayPath))
}

private fun createExportFileName(
    format: TaskExportFormat,
    clock: Clock
): String {
    val timestamp = LocalDateTime.now(clock)
        .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))

    return "task_decompose_$timestamp.${format.extension}"
}

private fun String.mimeTypeFromName(): String =
    when (substringAfterLast('.', missingDelimiterValue = "")) {
        TaskExportFormat.Json.extension -> TaskExportFormat.Json.mimeType
        else -> TaskExportFormat.Txt.mimeType
    }

private const val EXPORT_DIRECTORY = "TaskDecompose"
