package io.ii.presentation.components.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ripple
import androidx.compose.ui.Modifier

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
