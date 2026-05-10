package io.ii.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

@Composable
fun TaskDecomposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) {
            TaskDecomposeDarkColorScheme
        } else {
            TaskDecomposeLightColorScheme
        },
        typography = Typography(),
        content = content
    )
}
