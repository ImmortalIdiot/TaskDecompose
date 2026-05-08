package io.ii.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.ii.presentation.utils.StubScreen

@Composable
fun AppNavHost() { TaskDecomposeNavigation() }

@Composable
private fun TaskDecomposeNavigation() {
    val backStack = remember {
        mutableStateListOf<Route>(Route.TaskEditor)
    }

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) {
                backStack.removeLastOrNull()
            }
        },
        entryProvider = { route ->
            when (route) {
                Route.TaskEditor -> NavEntry(route) {
                    // TODO: implement TaskEditorScreen instead of stub
                    StubScreen(text = "TaskEditor Screen") { backStack.add(Route.History) }
                }

                Route.History -> NavEntry(route) {
                    // TODO: implement HistoryScreen instead of stub

                    StubScreen(text = "History Screen") { backStack.removeLastOrNull() }
                }
            }
        }
    )
}