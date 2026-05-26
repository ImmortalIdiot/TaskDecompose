package io.ii.presentation.components.bars

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ii.presentation.screens.PreviewScreen
import io.ii.presentation.theme.LocalDimensions

@Composable
internal fun TaskEditSnackbar(
    message: String?,
    isError: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
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
                MaterialTheme.colorScheme.inverseSurface
            },
            contentColor = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer
            } else {
                MaterialTheme.colorScheme.inverseOnSurface
            },
            tonalElevation = dimensions.other.elevation,
            shadowElevation = dimensions.other.elevation
        ) {
            Row(
                modifier = Modifier.padding(
                    start = dimensions.padding.paddingM,
                    top = dimensions.padding.padding12,
                    end = if (isError) dimensions.padding.padding4 else dimensions.padding.paddingM,
                    bottom = dimensions.padding.padding12
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = message.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium
                )

                if (isError) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun SuccessTaskEditSnackbarPreview() {
    PreviewScreen(
        content = {
            TaskEditSnackbar(
                message = "Успех",
                isError = false,
                onDismiss = {}
            )
        }
    )
}

@Preview
@Composable
private fun ErrorTaskEditSnackbarPreview() {
    PreviewScreen(
        content = {
            TaskEditSnackbar(
                message = "Ошибка",
                isError = true,
                onDismiss = {}
            )
        }
    )
}
