package io.ii.presentation.components.bars

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.ii.presentation.R
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.screens.PreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskEditorTopBar(
    showBackButton: Boolean,
    showCreateButton: Boolean,
    isSaveEnabled: Boolean,
    isDeleteEnabled: Boolean,
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(dimensions.padding.zero),
        colors = TaskDecomposeComponentDefaults.topAppBarColors(),
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        modifier = Modifier.size(dimensions.icon.iconM),
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        },
        title = {
            Text(stringResource(R.string.task_editor_item_title))
        },
        actions = {
            if (showCreateButton) {
                IconButton(onClick = onCreateClick) {
                    Icon(
                        modifier = Modifier.size(dimensions.icon.iconM),
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(
                onClick = onDeleteClick,
                enabled = isDeleteEnabled
            ) {
                Icon(
                    modifier = Modifier.size(dimensions.icon.iconM),
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            }

            IconButton(
                onClick = onSaveClick,
                enabled = isSaveEnabled
            ) {
                Icon(
                    modifier = Modifier.size(dimensions.icon.iconM),
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview
@Composable
private fun TaskEditorTopBarPreview() {
    PreviewScreen(
        content = {
            TaskEditorTopBar(
                showBackButton = true,
                showCreateButton = true,
                isSaveEnabled = true,
                isDeleteEnabled = true,
                onBackClick = {},
                onCreateClick = {},
                onSaveClick = {},
                onDeleteClick = {}
            )
        },
        alignment = Alignment.TopStart
    )
}
