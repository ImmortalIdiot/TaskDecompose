package io.ii.presentation.components.bars

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.ii.presentation.R
import io.ii.presentation.navigation.Route
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.screens.PreviewScreen

@Composable
internal fun TaskDecomposeBottomBar(
    selectedRoute: Route,
    onRouteClick: (Route) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = TaskDecomposeComponentDefaults.navigationBarContainerColor()
    ) {
        BottomBarItem(
            modifier = Modifier.weight(1f),
            selected = selectedRoute is Route.TaskEditor,
            enabled = selectedRoute !is Route.TaskEditor,
            onClick = { onRouteClick(Route.TaskEditor()) },
            icon = Icons.Outlined.AutoAwesome,
            text = stringResource(R.string.task_editor_item_title)
        )

        BottomBarItem(
            modifier = Modifier.weight(1f),
            selected = selectedRoute == Route.History,
            enabled = selectedRoute != Route.History,
            onClick = { onRouteClick(Route.History) },
            icon = Icons.Outlined.HistoryEdu,
            text = stringResource(R.string.history_item_title)
        )

        BottomBarItem(
            modifier = Modifier.weight(1f),
            selected = selectedRoute == Route.ModelSettings,
            enabled = selectedRoute != Route.ModelSettings,
            onClick = { onRouteClick(Route.ModelSettings) },
            icon = Icons.Outlined.Settings,
            text = stringResource(R.string.model_settings_item_title)
        )
    }
}

@Preview
@Composable
private fun TaskDecomposeBottomBarPreview() {

    var route by remember { mutableStateOf<Route>(Route.TaskEditor()) }

    PreviewScreen(
        alignment = Alignment.BottomStart,
        content = {
            TaskDecomposeBottomBar(
                selectedRoute = route,
                onRouteClick = { route = it }
            )
        }
    )
}
