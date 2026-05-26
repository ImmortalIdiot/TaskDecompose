package io.ii.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ii.presentation.R
import io.ii.presentation.components.bars.HistoryTopBar
import io.ii.presentation.components.bars.TaskExportSnackbar
import io.ii.presentation.components.cards.HistoryItemCard
import io.ii.presentation.components.other.ExportFormatDialog
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.utils.TaskExportFormat
import io.ii.presentation.utils.TaskExportResult
import io.ii.presentation.utils.openExportedTask
import io.ii.presentation.utils.saveTasksExport
import io.ii.presentation.utils.toExportableTask
import io.ii.presentation.viewmodels.HistoryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HistoryScreen(
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val dimensions = LocalDimensions.current
    var showClearHistoryDialog by rememberSaveable { mutableStateOf(false) }
    var showExportFormatDialog by rememberSaveable { mutableStateOf(false) }
    var exportResult by remember { mutableStateOf<TaskExportResult?>(null) }
    var exportErrorMessage by remember { mutableStateOf<String?>(null) }
    val exportSavedMessage = exportResult?.let { result ->
        stringResource(R.string.export_saved, result.displayPath)
    }
    val snackbarMessage = exportErrorMessage ?: exportSavedMessage
    val exportErrorText = stringResource(R.string.export_error)

    fun exportHistory(format: TaskExportFormat) {
        showExportFormatDialog = false
        exportResult = null
        exportErrorMessage = null

        val tasks = uiState.groups
            .flatMap { group -> group.tasks }
            .filter { item ->
                !uiState.hasSelection || item.task.id in uiState.selectedTaskIds
            }
            .map { item -> item.toExportableTask() }

        if (tasks.isEmpty()) {
            exportErrorMessage = exportErrorText
            return
        }

        runCatching {
            context.saveTasksExport(
                tasks = tasks,
                format = format
            )
        }.onSuccess { result ->
            exportResult = result
        }.onFailure {
            exportErrorMessage = exportErrorText
        }
    }

    if (showClearHistoryDialog) {
        ClearHistoryDialog(
            clearAllHistory = uiState.isAllSelected || !uiState.hasSelection,
            onConfirm = {
                showClearHistoryDialog = false
                viewModel.deleteSelectedOrAllHistory()
            },
            onDismiss = {
                showClearHistoryDialog = false
            }
        )
    }

    if (showExportFormatDialog) {
        ExportFormatDialog(
            onFormatClick = ::exportHistory,
            onDismiss = { showExportFormatDialog = false }
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HistoryTopBar(
                showDeleteButton = uiState.hasTasks,
                showExportButton = uiState.hasTasks,
                onDeleteClick = { showClearHistoryDialog = true },
                onExportClick = { showExportFormatDialog = true }
            )

            if (uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = TaskDecomposeComponentDefaults.progressColor(),
                    trackColor = TaskDecomposeComponentDefaults.progressTrackColor()
                )
            }

            when {
                uiState.errorMessage != null -> {
                    StubScreen(
                        text = uiState.errorMessage.orEmpty(),
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }

                uiState.groups.isEmpty() && !uiState.isLoading -> {
                    StubScreen(
                        text = stringResource(R.string.history_empty),
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = dimensions.padding.paddingM,
                            end = dimensions.padding.paddingM,
                            bottom = dimensions.padding.paddingM
                        ),
                        verticalArrangement = Arrangement.spacedBy(dimensions.padding.paddingM)
                    ) {
                        uiState.groups.forEach { group ->
                            item(key = group.date) {
                                Text(
                                    text = group.date,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            items(
                                items = group.tasks,
                                key = { item -> item.task.id }
                            ) { item ->
                                HistoryItemCard(
                                    title = item.task.title,
                                    description = item.task.description,
                                    llmModelName = item.llmModelName,
                                    subtasks = item.task.subtasks,
                                    isCompleted = item.task.isCompleted,
                                    isSelected = item.task.id in uiState.selectedTaskIds,
                                    onRootCompletedChange = { isCompleted ->
                                        viewModel.onRootCompletedChange(
                                            rootTaskId = item.task.id,
                                            isCompleted = isCompleted
                                        )
                                    },
                                    onSubtaskCompletedChange = { subtaskId, isCompleted ->
                                        viewModel.onSubtaskCompletedChange(
                                            rootTaskId = item.task.id,
                                            subtaskId = subtaskId,
                                            isCompleted = isCompleted
                                        )
                                    },
                                    onItemClick = {
                                        if (uiState.hasSelection) {
                                            viewModel.toggleTaskSelection(item.task.id)
                                        } else {
                                            onTaskClick(item.task.id)
                                        }
                                    },
                                    onItemLongClick = {
                                        viewModel.toggleTaskSelection(item.task.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        TaskExportSnackbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    start = dimensions.padding.paddingM,
                    end = dimensions.padding.paddingM,
                    bottom = dimensions.padding.paddingM
                ),
            message = snackbarMessage,
            isError = exportErrorMessage != null,
            onDismiss = {
                exportErrorMessage = null
                exportResult = null
            },
            actionText = exportResult?.let { stringResource(R.string.export_open) },
            onActionClick = exportResult?.let { result ->
                {
                    runCatching { context.openExportedTask(result) }
                        .onFailure {
                            exportErrorMessage = exportErrorText
                        }
                }
            }
        )
    }
}

@Composable
private fun ClearHistoryDialog(
    clearAllHistory: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.history_clear_dialog_title))
        },
        text = {
            Text(
                text = stringResource(
                    if (clearAllHistory) {
                        R.string.history_clear_all_dialog_text
                    } else {
                        R.string.history_clear_selected_dialog_text
                    }
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(R.string.history_clear_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.history_clear_cancel))
            }
        }
    )
}
