package io.ii.presentation.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.ii.presentation.components.bars.TaskDecomposeBottomBar
import io.ii.presentation.components.other.TaskDecomposeContent
import io.ii.presentation.utils.RouteBackStackSaver

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
            TaskDecomposeBottomBar(
                selectedRoute = selectedRoute,
                onRouteClick = { navigateTo(it) }
            )
        }
    ) { paddingValues ->
        TaskDecomposeContent(
            modifier = Modifier.padding(paddingValues),
            selectedRoute = selectedRoute
        )
    }
}
