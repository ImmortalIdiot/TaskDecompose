package io.ii.presentation.components.scaffolds

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ii.presentation.components.bars.TaskEditorTopBar
import io.ii.presentation.screens.TaskEditScreen
import io.ii.presentation.viewmodels.TaskEditViewModel

@Composable
internal fun TaskEditScaffold(
    viewModel: TaskEditViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TaskEditorTopBar(
                isSaveEnabled = uiState.title.isNotBlank() && !uiState.isLoading,
                isDeleteEnabled = !uiState.isLoading,
                onSaveClick = viewModel::saveTask,
                onDeleteClick = viewModel::deleteTask
            )
        }
    ) { paddingValues ->
        TaskEditScreen(
            modifier = Modifier.padding(paddingValues)
        )
    }
}
