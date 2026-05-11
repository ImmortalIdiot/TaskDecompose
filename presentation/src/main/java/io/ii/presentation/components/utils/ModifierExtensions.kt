package io.ii.presentation.components.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier

/**
 * Делает компонент кликабельным с собственным ripple эффектом.
 *
 * @param enabled доступен ли клик
 * @param interactionSource источник взаимодействий для clickable
 * @param onClick действие при нажатии
 *
 * @return [Modifier] с обработчиком клика и ripple индикацией
 */
internal fun Modifier.niceRippleClickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: () -> Unit
): Modifier = this.then(
    Modifier.clickable(
        interactionSource = interactionSource,
        indication = ripple(bounded = true),
        enabled = enabled,
        onClick = onClick
    )
)

/**
 * Делает компонент кликабельным без визуальной индикации нажатия.
 *
 * @param enabled доступен ли клик
 * @param interactionSource источник взаимодействий для clickable
 * @param onClick действие при нажатии
 *
 * @return [Modifier] с обработчиком клика без ripple эффекта
 */
internal fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    onClick: () -> Unit
): Modifier = this.then(
    Modifier.clickable(
        interactionSource = interactionSource,
        indication = null,
        enabled = enabled,
        onClick = onClick
    )
)
