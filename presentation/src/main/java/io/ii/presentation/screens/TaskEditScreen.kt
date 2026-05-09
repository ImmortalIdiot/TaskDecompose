package io.ii.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.ii.presentation.components.cards.DecompositionParamsCard
import io.ii.presentation.components.cards.TaskTreeCard
import io.ii.presentation.components.inputs.OptionalDescriptionInput
import io.ii.presentation.components.inputs.TaskTitleInput
import io.ii.presentation.states.TaskEditorUiState
import io.ii.presentation.utils.LocalDimensions

@Composable
internal fun TaskEditScreen(
    uiState: TaskEditorUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDepthChange: (Int) -> Unit,
    onPriorityChange: (Boolean) -> Unit,
    onDecomposeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    var isDescriptionExpanded by rememberSaveable { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(dimensions.padding.paddingM),
        verticalArrangement = Arrangement.spacedBy(dimensions.padding.paddingM)
    ) {
        item {
            TaskTitleInput(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.title,
                isLoading = uiState.isLoading,
                onValueChange = onTitleChange,
                onDecomposeClick = onDecomposeClick
            )
        }

        item {
            OptionalDescriptionInput(
                modifier = Modifier.fillMaxWidth(),
                value = uiState.description,
                expanded = isDescriptionExpanded,
                onExpandedChange = { isDescriptionExpanded = it },
                onValueChange = onDescriptionChange
            )
        }

        item {
            DecompositionParamsCard(
                modifier = Modifier.fillMaxWidth(),
                depth = uiState.depth,
                hasPriority = uiState.hasPriority,
                onDepthChange = onDepthChange,
                onPriorityChange = onPriorityChange
            )
        }

        if (uiState.isLoading) {
            item {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }

        if (uiState.subtasks.isNotEmpty()) {
            item {
                TaskTreeCard(
                    modifier = Modifier.fillMaxWidth(),
                    rootTitle = uiState.title,
                    subtasks = uiState.subtasks
                )
            }
        }
    }
}
