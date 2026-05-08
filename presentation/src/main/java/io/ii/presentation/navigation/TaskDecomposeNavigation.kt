package io.ii.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.ii.presentation.R
import io.ii.presentation.components.bars.BottomBarItem
import io.ii.presentation.utils.Constants
import io.ii.presentation.utils.RouteBackStackSaver
import io.ii.presentation.utils.StubScreen

@Composable
fun AppNavHost() { TaskDecomposeNavigation() }

@Composable
private fun TaskDecomposeNavigation(
    modifier: Modifier = Modifier
) {
    var backStack by rememberSaveable(
        stateSaver = RouteBackStackSaver
    ) {
        mutableStateOf(listOf<Route>(Route.TaskEditor))
    }

    val selectedRoute = backStack.last()

    fun navigateTo(route: Route) {
        backStack = when (route) {
            Route.TaskEditor -> { listOf(Route.TaskEditor) }

            Route.History -> {
                if (selectedRoute == Route.History) {
                    backStack
                } else {
                    Route.ALL
                }
            }
        }
    }

    BackHandler(enabled = backStack.size > 1) {
        backStack = backStack.dropLast(1)
    }

    Scaffold(
        modifier = modifier,
        containerColor = Color(0xFF57B9FF), // TODO: replace with screens background color
        bottomBar = {
            NavigationBar {
                BottomBarItem(
                    modifier = Modifier.weight(1f),
                    selected = selectedRoute == Route.TaskEditor,
                    onClick = { navigateTo(Route.TaskEditor) },
                    icon = Icons.Outlined.AutoAwesome,
                    text = stringResource(R.string.task_editor_item_title)
                )

                BottomBarItem(
                    modifier = Modifier.weight(1f),
                    selected = selectedRoute == Route.History,
                    onClick = { navigateTo(Route.History) },
                    icon = Icons.Outlined.HistoryEdu,
                    text = stringResource(R.string.history_item_title)
                )
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            modifier = Modifier.padding(paddingValues),
            targetState = selectedRoute,
            transitionSpec = {
                val direction = if (
                    targetState.index > initialState.index
                ) {
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
                    StubScreen(text = stringResource(R.string.task_editor_item_title))
                }

                Route.History -> {
                    StubScreen(
                        text = stringResource(R.string.history_item_title),
                        backgroundColor = Color(0xFFFFC857)
                    )
                }
            }
        }
    }
}
