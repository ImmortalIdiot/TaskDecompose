package io.ii.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
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

private const val TASK_TITLE_ITEM_KEY = "title"
private const val TASK_DESCRIPTION_ITEM_KEY = "description"
private const val TASK_PARAMS_ITEM_KEY = "params"
private const val TASK_PROGRESS_INDICATOR_ITEM_KEY = "progress_indicator"
private const val TASK_TREE_ITEM_KEY = "task_tree"

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
    val listState = rememberLazyListState()
    var listBounds by remember { mutableStateOf<Rect?>(null) }

    var inlineDecomposeButtonBounds by remember { mutableStateOf<Rect?>(null) }
    val showDecomposeButtonInTopBar by remember {
        derivedStateOf {
            val viewport = listBounds
            val button = inlineDecomposeButtonBounds
            val layoutInfo = listState.layoutInfo
            val titleItem = layoutInfo.visibleItemsInfo.firstOrNull { item ->
                item.key == TASK_TITLE_ITEM_KEY
            }

            layoutInfo.totalItemsCount > 0 && (
                titleItem == null || (
                    viewport != null && button != null && !viewport.contains(button)
                )
            )
        }
    }

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
            showDecomposeButton = showDecomposeButtonInTopBar,
            isDecomposeEnabled = uiState.canDecompose,
            isSaveEnabled = uiState.title.isNotBlank() && !uiState.isLoading,
            isDeleteEnabled = !uiState.isLoading,
            onBackClick = onBackClick,
            onCreateClick = {
                viewModel.createNewTask()
                onCreateClick()
            },
            onDecomposeClick = viewModel::decomposeTask,
            onSaveClick = viewModel::saveTask,
            onDeleteClick = viewModel::deleteTask
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    listBounds = coordinates.boundsInRoot()
                },
            state = listState,
            contentPadding = PaddingValues(
                start = dimensions.padding.paddingM,
                end = dimensions.padding.paddingM,
                bottom = dimensions.padding.paddingM
            ),
            verticalArrangement = Arrangement.spacedBy(dimensions.padding.paddingM)
        ) {
            item(key = TASK_TITLE_ITEM_KEY) {
                TaskTitleInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.title,
                    isLoading = uiState.isLoading,
                    onValueChange = viewModel::onTitleChange,
                    onDecomposeClick = viewModel::decomposeTask,
                    onDecomposeButtonBoundsChange = { bounds ->
                        inlineDecomposeButtonBounds = bounds
                    }
                )
            }

            item(key = TASK_DESCRIPTION_ITEM_KEY) {
                OptionalDescriptionInput(
                    modifier = Modifier.fillMaxWidth(),
                    value = uiState.description,
                    expanded = isDescriptionExpanded,
                    onExpandedChange = { isDescriptionExpanded = it },
                    onValueChange = viewModel::onDescriptionChange
                )
            }

            item(key = TASK_PARAMS_ITEM_KEY) {
                DecompositionParamsCard(
                    modifier = Modifier.fillMaxWidth(),
                    depth = uiState.depth,
                    hasPriority = uiState.hasPriority,
                    onDepthChange = viewModel::onDepthChange,
                    onPriorityChange = viewModel::onPriorityChange
                )
            }

            if (uiState.isLoading) {
                item(key = TASK_PROGRESS_INDICATOR_ITEM_KEY) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = TaskDecomposeComponentDefaults.progressColor(),
                        trackColor = TaskDecomposeComponentDefaults.progressTrackColor()
                    )
                }
            }

            if (!uiState.isLoading && uiState.subtasks.isNotEmpty()) {
                item(key = TASK_TREE_ITEM_KEY) {
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

private fun Rect.contains(other: Rect): Boolean =
    other.left >= left &&
            other.top >= top &&
            other.right <= right &&
            other.bottom <= bottom
