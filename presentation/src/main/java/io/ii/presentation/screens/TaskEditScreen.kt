package io.ii.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ii.presentation.components.bars.TaskEditorTopBar
import io.ii.presentation.components.cards.DecompositionParamsCard
import io.ii.presentation.components.cards.TaskTreeCard
import io.ii.presentation.components.inputs.OptionalDescriptionInput
import io.ii.presentation.components.inputs.TaskTitleInput
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.viewmodels.TaskEditViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun TaskEditScreen(
    taskId: String? = null,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    viewModel: TaskEditViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isDescriptionExpanded by rememberSaveable { mutableStateOf(false) }

    val dimensions = LocalDimensions.current

    LaunchedEffect(taskId) {
        if (taskId == null) {
            viewModel.createNewTask()
        } else {
            viewModel.loadTask(taskId)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TaskEditorTopBar(
            showBackButton = canNavigateBack && uiState.id != null,
            showCreateButton = uiState.id != null,
            isSaveEnabled = uiState.title.isNotBlank() && !uiState.isLoading,
            isDeleteEnabled = !uiState.isLoading,
            onBackClick = onBackClick,
            onCreateClick = {
                viewModel.createNewTask()
                onCreateClick()
            },
            onSaveClick = viewModel::saveTask,
            onDeleteClick = viewModel::deleteTask
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = dimensions.padding.paddingM,
                end = dimensions.padding.paddingM,
                bottom = dimensions.padding.paddingM
            ),
            verticalArrangement = Arrangement.spacedBy(dimensions.padding.paddingM)
        ) {
            item(key = "title") {
                TaskTitleInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.title,
                    isLoading = uiState.isLoading,
                    onValueChange = viewModel::onTitleChange,
                    onDecomposeClick = viewModel::decomposeTask
                )
            }

            item(key = "description") {
                OptionalDescriptionInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.description,
                    expanded = isDescriptionExpanded,
                    onExpandedChange = { isDescriptionExpanded = it },
                    onValueChange = viewModel::onDescriptionChange
                )
            }

            item(key = "params") {
                DecompositionParamsCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = uiState.depth,
                    hasPriority = uiState.hasPriority,
                    onDepthChange = viewModel::onDepthChange,
                    onPriorityChange = viewModel::onPriorityChange
                )
            }

            if (uiState.isLoading) {
                item(key = "progress") {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = TaskDecomposeComponentDefaults.progressColor(),
                        trackColor = TaskDecomposeComponentDefaults.progressTrackColor()
                    )
                }
            }

            if (!uiState.isLoading && uiState.subtasks.isNotEmpty()) {
                item(key = "task_tree") {
                    TaskTreeCard(
                        modifier = Modifier.fillMaxWidth(),
                        rootTitle = uiState.title,
                        subtasks = uiState.subtasks
                    )
                }
            }
        }
    }
}
