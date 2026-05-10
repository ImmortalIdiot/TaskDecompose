package io.ii.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import io.ii.presentation.R
import io.ii.presentation.utils.StubScreen

// TODO: implement screen
@Composable
internal fun HistoryScreen(
    modifier: Modifier = Modifier
) {
    StubScreen(
        modifier = modifier,
        text = stringResource(R.string.history_item_title),
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer
    )
}
