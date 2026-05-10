package io.ii.presentation.components.other

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ii.presentation.navigation.Route
import io.ii.presentation.screens.HistoryScreen
import io.ii.presentation.screens.TaskEditScreen
import io.ii.presentation.utils.Constants

@Composable
internal fun TaskDecomposeContent(
    selectedRoute: Route,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        modifier = modifier,
        targetState = selectedRoute,
        transitionSpec = {
            val direction = if (targetState.index > initialState.index) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }

            slideIntoContainer(
                towards = direction,
                animationSpec = tween(durationMillis = Constants.SLIDE_ANIMATION_DURATION)
            ) + fadeIn(
                animationSpec = tween(durationMillis = Constants.FADE_ANIMATION_DURATION)
            ) togetherWith slideOutOfContainer(
                towards = direction,
                animationSpec = tween(durationMillis = Constants.SLIDE_ANIMATION_DURATION)
            ) + fadeOut(
                animationSpec = tween(durationMillis = Constants.FADE_ANIMATION_DURATION)
            )
        }
    ) { route ->
        when (route) {
            Route.TaskEditor -> {
                TaskEditScreen()
            }

            Route.History -> {
                HistoryScreen()
            }
        }
    }
}