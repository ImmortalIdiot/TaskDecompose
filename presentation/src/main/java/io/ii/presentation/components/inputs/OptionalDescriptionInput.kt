package io.ii.presentation.components.inputs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.ii.presentation.R
import io.ii.presentation.components.utils.noRippleClickable
import io.ii.presentation.utils.LocalDimensions
import io.ii.presentation.utils.PreviewScreen

@Composable
internal fun OptionalDescriptionInput(
    value: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(dimensions.padding.paddingM)
                .noRippleClickable { onExpandedChange(!expanded) }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.task_condensed_description),
                    style = MaterialTheme.typography.titleMedium
                )

                Icon(
                    imageVector = if (expanded) {
                        Icons.Rounded.ExpandLess
                    } else {
                        Icons.Rounded.ExpandMore
                    },
                    contentDescription = null
                )
            }

            AnimatedVisibility(visible = expanded) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = dimensions.padding.padding12),
                    value = value,
                    onValueChange = onValueChange,
                    label = {
                        Text(
                            text = stringResource(R.string.task_description_label)
                        )
                    },
                    minLines = 3,
                    maxLines = 7
                )
            }
        }
    }
}

@Preview
@Composable
private fun OptionalDescriptionInputPreview() {
    var input by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    PreviewScreen(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OptionalDescriptionInput(
                    value = "",
                    expanded = false,
                    onExpandedChange = {},
                    onValueChange = {}
                )
                OptionalDescriptionInput(
                    value = "",
                    expanded = true,
                    onExpandedChange = {},
                    onValueChange = {}
                )
                OptionalDescriptionInput(
                    value = "task description text",
                    expanded = false,
                    onExpandedChange = {},
                    onValueChange = {}
                )
                OptionalDescriptionInput(
                    value = input,
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    onValueChange = { input = it }
                )
            }
        }
    )
}
