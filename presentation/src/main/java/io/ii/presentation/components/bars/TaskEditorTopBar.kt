package io.ii.presentation.components.bars

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
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
import io.ii.presentation.utils.LocalDimensions
import io.ii.presentation.utils.PreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskEditorTopBar(
    isSaveEnabled: Boolean,
    isDeleteEnabled: Boolean,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(dimensions.padding.zero),
        title = {
            Text(stringResource(R.string.task_editor_item_title))
        },
        actions = {
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
                isSaveEnabled = true,
                isDeleteEnabled = true,
                onSaveClick = {},
                onDeleteClick = {}
            )
        },
        alignment = Alignment.TopStart
    )
}
