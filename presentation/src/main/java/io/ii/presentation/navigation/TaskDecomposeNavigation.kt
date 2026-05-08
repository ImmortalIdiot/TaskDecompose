package io.ii.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                NavigationBarItem(
                    selected = selectedRoute == Route.TaskEditor,
                    onClick = {
                        selectedRoute = Route.TaskEditor
                    },
                    alwaysShowLabel = true,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Задача",
                            fontWeight = if (selectedRoute == Route.TaskEditor) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            }
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    )
                )

                NavigationBarItem(
                    selected = selectedRoute == Route.History,
                    onClick = { selectedRoute = Route.History },
                    icon = {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Outlined.HistoryEdu,
                            contentDescription = null
                        )
                    },
                    label = {
                        Text(text = "History")
                    }
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
