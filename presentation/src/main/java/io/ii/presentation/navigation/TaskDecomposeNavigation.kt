package io.ii.presentation.navigation

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
import io.ii.presentation.components.bars.BottomBarItem
import io.ii.presentation.utils.RouteStateSaver
import io.ii.presentation.utils.StubScreen

@Composable
fun AppNavHost() { TaskDecomposeNavigation() }

@Composable
private fun TaskDecomposeNavigation(
    modifier: Modifier = Modifier
) {
    var selectedRoute by rememberSaveable(stateSaver = RouteStateSaver) {
        mutableStateOf(Route.TaskEditor)
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                BottomBarItem(
                    modifier = Modifier.weight(1f),
                    selected = selectedRoute == Route.TaskEditor,
                    onClick = { selectedRoute = Route.TaskEditor },
                    icon = Icons.Outlined.AutoAwesome,
                    text = "Task edit"
                )

                BottomBarItem(
                    modifier = Modifier.weight(1f),
                    selected = selectedRoute == Route.History,
                    onClick = { selectedRoute = Route.History },
                    icon = Icons.Outlined.HistoryEdu,
                    text = "History"
                )
            }
        }
    ) { paddingValues ->
        when (selectedRoute) {
            Route.TaskEditor -> {
                StubScreen(
                    modifier = Modifier.padding(paddingValues),
                    text = "Создание задачи"
                )
            }

            Route.History -> {
                StubScreen(
                    modifier = Modifier.padding(paddingValues),
                    text = "История",
                    backgroundColor = Color(0xFFFFC857)
                )
            }
        }
    }
}
