package io.ii.presentation.components.bars

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import io.ii.presentation.R
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.screens.PreviewScreen
import io.ii.presentation.theme.LocalDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryTopBar(
    showDeleteButton: Boolean = false,
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    TopAppBar(
        modifier = modifier,
        windowInsets = WindowInsets(dimensions.padding.zero),
        colors = TaskDecomposeComponentDefaults.topAppBarColors(),
        title = {
            Text(
                text = stringResource(R.string.history_item_title),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            if (showDeleteButton) {
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        modifier = Modifier.size(dimensions.icon.iconM),
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            }
        }
    )
}

@Preview
@Composable
private fun TaskEditorTopBarPreview() {
    PreviewScreen(
        content = { HistoryTopBar() },
        alignment = Alignment.TopStart
    )
}
