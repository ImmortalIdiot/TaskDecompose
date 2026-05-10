package io.ii.presentation.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ii.presentation.R
import io.ii.presentation.utils.LocalDimensions
import io.ii.presentation.utils.PreviewScreen
import kotlin.math.roundToInt

@Composable
internal fun DecompositionParamsCard(
    depth: Int,
    hasPriority: Boolean,
    onDepthChange: (Int) -> Unit,
    onPriorityChange: (Boolean) -> Unit,
    minDepth: Float = 2f,
    maxDepth: Float = 5f,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    val steps = remember(minDepth, maxDepth) {
        (maxDepth - minDepth).toInt() - 1
    }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(dimensions.padding.paddingM),
            verticalArrangement = Arrangement.spacedBy(dimensions.padding.padding12)
        ) {
            Text(
                text = stringResource(R.string.task_decomposition_params    ),
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = stringResource(R.string.task_decomposition_depth, depth),
                style = MaterialTheme.typography.bodyMedium
            )

            // TODO: modify slider
            Slider(
                value = depth.toFloat(),
                onValueChange = { onDepthChange(it.roundToInt()) },
                valueRange = minDepth..maxDepth,
                steps = steps,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium,
                    text = stringResource(R.string.task_decomposition_priority)
                )

                Switch(
                    checked = hasPriority,
                    onCheckedChange = onPriorityChange
                )
            }
        }
    }
}

@Preview
@Composable
private fun DecompositionParamsCardPreview() {

    var depth by remember { mutableIntStateOf(2) }
    var priority by remember { mutableStateOf(true) }

    PreviewScreen(
        content = {
            DecompositionParamsCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                depth = depth,
                hasPriority = priority,
                onDepthChange = { depth = it },
                onPriorityChange = { priority = !priority }
            )
        }
    )
}
