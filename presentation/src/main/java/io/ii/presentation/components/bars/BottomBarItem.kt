package io.ii.presentation.components.bars

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.ii.presentation.components.utils.niceRippleClickable
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.theme.LocalDimensions

@Composable
internal fun BottomBarItem(
    selected: Boolean,
    text: String,
    icon: ImageVector,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = if (selected) {
        TaskDecomposeComponentDefaults.selectedNavigationContentColor()
    } else {
        TaskDecomposeComponentDefaults.unselectedNavigationContentColor()
    }

    val dimensions = LocalDimensions.current

    val barItemShape = remember { RoundedCornerShape(dimensions.corner.cornerShapeM) }

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .clip(barItemShape)
            .niceRippleClickable(
                enabled = enabled,
                interactionSource = interactionSource,
                onClick = onClick
            )
            .padding(
                horizontal = dimensions.padding.paddingM,
                vertical = dimensions.padding.paddingS
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(dimensions.icon.iconL),
            tint = contentColor
        )

        Text(
            text = text,
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            }
        )
    }
}
