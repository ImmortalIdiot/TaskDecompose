package io.ii.presentation.components.bars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import io.ii.presentation.R
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.screens.PreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TaskEditorTopBar(
    showBackButton: Boolean,
    showCreateButton: Boolean,
    showDecomposeButton: Boolean,
    isDecomposeEnabled: Boolean,
    isSaveEnabled: Boolean,
    isDeleteEnabled: Boolean,
    onBackClick: () -> Unit,
    onCreateClick: () -> Unit,
    onDecomposeClick: () -> Unit,
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
            Text(
                text = stringResource(R.string.task_editor_item_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    lineHeight = 20.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
                    tint = if (isSaveEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            AnimatedVisibility(
                visible = showDecomposeButton,
                enter = fadeIn() +
                        slideInVertically(initialOffsetY = { height -> height }) +
                        expandHorizontally(expandFrom = Alignment.End) +
                        scaleIn(),
                exit = fadeOut() +
                        slideOutVertically(targetOffsetY = { height -> height }) +
                        shrinkHorizontally(shrinkTowards = Alignment.End) +
                        scaleOut()
            ) {
                IconButton(
                    onClick = onDecomposeClick,
                    enabled = isDecomposeEnabled
                ) {
                    Icon(
                        modifier = Modifier.size(dimensions.icon.iconM),
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
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
        content = {
            TaskEditorTopBar(
                showBackButton = true,
                showCreateButton = true,
                showDecomposeButton = true,
                isDecomposeEnabled = true,
                isSaveEnabled = true,
                isDeleteEnabled = true,
                onBackClick = {},
                onCreateClick = {},
                onDecomposeClick = {},
                onSaveClick = {},
                onDeleteClick = {}
            )
        },
        alignment = Alignment.TopStart
    )
}
