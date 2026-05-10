package io.ii.presentation.components.bars

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.ii.presentation.R
import io.ii.presentation.theme.TaskDecomposeComponentDefaults
import io.ii.presentation.utils.PreviewScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryTopBar() {
    TopAppBar(
        colors = TaskDecomposeComponentDefaults.topAppBarColors(),
        title = {
            Text(stringResource(R.string.history_item_title))
        }
    )
}

@Preview
@Composable
private fun TaskEditorTopBarPreview() {
    PreviewScreen(
        content = { HistoryTopBar() },
        alignment = Alignment.TopStart
    )
}
