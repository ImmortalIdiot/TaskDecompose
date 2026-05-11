package io.ii.presentation.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * CompositionLocal с размерами дизайн-системы приложения.
 */
internal val LocalDimensions = compositionLocalOf { Dimensions() }

/**
 * Набор размеров, используемых в UI.
 *
 * @property padding отступы
 * @property icon размеры иконок
 * @property corner радиусы скруглений
 * @property other специализированные размеры компонентов
 */
internal data class Dimensions(
    val padding: Paddings = Paddings(),
    val icon: Icons = Icons(),
    val corner: Corners = Corners(),
    val other: Other = Other()
)

/**
 * Стандартные отступы приложения.
 */
internal data class Paddings(
    val paddingS: Dp = 8.dp,
    val paddingM: Dp = 16.dp,
    val paddingL: Dp = 20.dp,

    val padding4: Dp = 4.dp,
    val padding12: Dp = 12.dp,

    val zero: Dp = 0.dp
)

/**
 * Стандартные размеры иконок.
 */
internal data class Icons(
    val iconM: Dp = 24.dp,
    val iconL: Dp = 32.dp
)

/**
 * Радиусы скругления компонентов.
 */
internal data class Corners(
    val cornerShapeM: Dp = 20.dp
)

/**
 * Специализированные размеры, не относящиеся к общим отступам, иконкам или скруглениям.
 */
internal data class Other(
    val sliderHeight: Dp = 12.dp,

    val treeLineStrokeWidth: Dp = 2.dp,
    val branchRadius: Dp = 8.dp,
    val centerLineHeight: Dp = 14.dp
)
