package io.ii.presentation.components.spacers

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
internal fun RowScope.HorizontalSpacer(
    width: Dp,
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier.width(width))
}
