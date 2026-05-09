package io.ii.presentation.components.inputs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import io.ii.presentation.utils.LocalDimensions
import io.ii.presentation.utils.PreviewScreen

@Composable
internal fun TaskTitleInput(
    value: String,
    isLoading: Boolean,
    onValueChange: (String) -> Unit,
    onDecomposeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(dimensions.padding.paddingS)
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = stringResource(R.string.task_title_label)
                )
            },
            minLines = 1,
            maxLines = 5,
            enabled = !isLoading
        )

        IconButton(
            modifier = Modifier.align(Alignment.CenterVertically),
            onClick = onDecomposeClick,
            enabled = value.isNotBlank() && !isLoading
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = null
            )
        }
    }
}

@Preview
@Composable
private fun PreviewTaskTitleInput() {

    var inputValue by remember { mutableStateOf("") }

    PreviewScreen(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TaskTitleInput(
                    value = "Текст основной задачи",
                    isLoading = false,
                    onValueChange = {},
                    onDecomposeClick = {}
                )

                TaskTitleInput(
                    value = inputValue,
                    isLoading = false,
                    onValueChange = { inputValue = it },
                    onDecomposeClick = {}
                )

                TaskTitleInput(
                    value = "Задача",
                    isLoading = true,
                    onValueChange = {},
                    onDecomposeClick = {}
                )
            }
        }
    )
}
