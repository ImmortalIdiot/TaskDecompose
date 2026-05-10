package io.ii.presentation.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.ii.presentation.theme.TaskDecomposeTheme

@Composable
internal fun PreviewScreen(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center
) {
    val scrollState = rememberScrollState()

    TaskDecomposeTheme {
        Box(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(color = MaterialTheme.colorScheme.background),
            contentAlignment = alignment
        ) {
            content()
        }
    }
}
