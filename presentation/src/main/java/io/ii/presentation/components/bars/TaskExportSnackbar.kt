package io.ii.presentation.components.bars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ii.presentation.R
import io.ii.presentation.screens.PreviewScreen
import io.ii.presentation.theme.LocalDimensions

@Composable
internal fun TaskExportSnackbar(
    message: String?,
    isError: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    val dimensions = LocalDimensions.current

    AnimatedVisibility(
        modifier = modifier,
        visible = !message.isNullOrBlank(),
        enter = fadeIn() + slideInVertically(initialOffsetY = { height -> height }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { height -> height })
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = if (isError) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
            contentColor = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            border = BorderStroke(
                width = 1.dp,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.outlineVariant
                }
            ),
            tonalElevation = dimensions.other.elevation,
            shadowElevation = dimensions.other.elevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensions.padding.paddingM,
                        top = dimensions.padding.padding12,
                        end = dimensions.padding.paddingM,
                        bottom = dimensions.padding.padding4
                    )
            ) {
                Text(
                    text = message.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!actionText.isNullOrBlank() && onActionClick != null) {
                        TextButton(
                            onClick = onActionClick,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = if (isError) {
                                    MaterialTheme.colorScheme.onErrorContainer
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        ) {
                            Text(text = actionText)
                        }
                    }

                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (isError) {
                                MaterialTheme.colorScheme.onErrorContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    ) {
                        Text(text = stringResource(R.string.export_cancel))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TaskExportSnackbarPreview() {
    PreviewScreen(
        content = {
            TaskExportSnackbar(
                message = "Сохранено: Download/TaskDecompose/task_decompose_20260526_150753.json",
                isError = false,
                onDismiss = {},
                actionText = "Открыть",
                onActionClick = {}
            )
        }
    )
}
