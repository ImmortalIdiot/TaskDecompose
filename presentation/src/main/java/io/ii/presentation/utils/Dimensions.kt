package io.ii.presentation.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val LocalDimensions = compositionLocalOf { Dimensions() }

internal data class Dimensions(
    val padding: Paddings = Paddings(),
    val icon: Icons = Icons(),
    val corner: Corners = Corners()
)

internal data class Paddings(
    val paddingS: Dp = 8.dp,
    val paddingM: Dp = 16.dp,
    val paddingL: Dp = 20.dp,

)

internal data class Icons(
    val iconL: Dp = 32.dp
)

internal data class Corners(
    val cornerShapeM: Dp = 20.dp
)
