package io.ii.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ii.presentation.R
import io.ii.presentation.components.bars.HistoryTopBar
import io.ii.presentation.components.cards.HistoryItemCard
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.viewmodels.HistoryViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HistoryScreen(
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current
    var showClearHistoryDialog by rememberSaveable { mutableStateOf(false) }

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

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HistoryTopBar(
            showDeleteButton = uiState.hasTasks,
            onDeleteClick = { showClearHistoryDialog = true }
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
                                isSelected = item.task.id in uiState.selectedTaskIds,
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
