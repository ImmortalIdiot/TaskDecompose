package io.ii.presentation.components.scaffolds

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ii.presentation.components.bars.HistoryTopBar
import io.ii.presentation.components.bars.TaskEditorTopBar
import io.ii.presentation.screens.HistoryScreen
import io.ii.presentation.screens.TaskEditScreen

// TODO: implement scaffold
@Composable
internal fun HistoryScaffold() {
    Scaffold(
        topBar = {
            HistoryTopBar()
        }
    ) { paddingValues ->
        HistoryScreen(modifier = Modifier.padding(paddingValues))
    }
}