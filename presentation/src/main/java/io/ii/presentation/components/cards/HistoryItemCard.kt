package io.ii.presentation.components.cards

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import io.ii.presentation.components.spacers.HorizontalSpacer
import io.ii.presentation.components.spacers.VerticalSpacer
import io.ii.presentation.components.utils.niceRippleClickable
import io.ii.presentation.states.TaskEditorItemUiState
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.utils.Constants
import io.ii.presentation.utils.LocalDimensions
import io.ii.presentation.utils.PreviewScreen

@Composable
internal fun HistoryItemCard(
    title: String,
    description: String?,
    subtasks: List<TaskEditorItemUiState>,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current
    var expanded by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .niceRippleClickable(onClick = onItemClick),
        colors = TaskDecomposeComponentDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(dimensions.padding.paddingM)
        ) {
            HistoryItemHeader(
                title = title,
                description = description,
                expanded = expanded,
                onClick = { expanded = !expanded }
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(expandFrom = Alignment.Top),
                exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Top)
            ) {
                Column {
                    VerticalSpacer(dimensions.padding.paddingM)

                    TaskTreeCard(
                        modifier = Modifier.fillMaxWidth(),
                        rootTitle = title,
                        animationSpeedCoefficient = 4f,
                        subtasks = subtasks
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryItemHeader(
    title: String,
    description: String?,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .padding(vertical = dimensions.padding.paddingS),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!description.isNullOrBlank()) {
                VerticalSpacer(dimensions.padding.padding4)

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = if (expanded) Int.MAX_VALUE else 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        HorizontalSpacer(dimensions.padding.paddingS)

        Icon(
            modifier = Modifier
                .size(dimensions.icon.iconM)
                .niceRippleClickable(onClick = onClick)
                .rotate(arrowRotation),
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun HistoryItemCardPreview() {
    val taskText = "Очистить компьютер от мусора"

    PreviewScreen(
        alignment = Alignment.TopStart,
        content = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                HistoryItemCard(
                    title = taskText,
                    description = null,
                    onItemClick = {},
                    subtasks = Constants.MOCK_SUBTASKS
                )

                HistoryItemCard(
                    title = taskText,
                    description = LoremIpsum(100).values.first(),
                    onItemClick = {},
                    subtasks = Constants.MOCK_SUBTASKS
                )
            }
        }
    )
}
