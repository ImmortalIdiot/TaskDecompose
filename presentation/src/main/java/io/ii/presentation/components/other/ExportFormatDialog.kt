package io.ii.presentation.components.other

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.ii.presentation.R
import io.ii.presentation.utils.TaskExportFormat

@Composable
internal fun ExportFormatDialog(
    onFormatClick: (TaskExportFormat) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.export_format_title))
        },
        confirmButton = {
            TextButton(onClick = { onFormatClick(TaskExportFormat.Json) }) {
                Text(text = stringResource(R.string.export_format_json))
            }
        },
        dismissButton = {
            TextButton(onClick = { onFormatClick(TaskExportFormat.Txt) }) {
                Text(text = stringResource(R.string.export_format_txt))
            }
        }
    )
}
