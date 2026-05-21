package io.ii.presentation.components.other

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.ii.domain.model.LlmSettings
import io.ii.presentation.R
import io.ii.presentation.states.LlmSettingsUiState
import io.ii.presentation.theme.LocalDimensions

@Composable
internal fun LlmTabs(
    uiState: LlmSettingsUiState,
    onModelSelect: (String) -> Unit,
    onAddCustomModel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTabIndex = if (uiState.isGigaChatSelected) {
        0
    } else {
        uiState.customModels.indexOfFirst { model ->
            model.id == uiState.selectedModelId
        }.takeIf { index -> index >= 0 }?.plus(1) ?: 0
    }

    SecondaryScrollableTabRow(
        modifier = modifier.fillMaxWidth(),
        selectedTabIndex = selectedTabIndex,
        edgePadding = LocalDimensions.current.padding.zero
    ) {
        Tab(
            selected = uiState.isGigaChatSelected,
            onClick = { onModelSelect(LlmSettings.GIGACHAT_MODEL_ID) },
            text = { Text(text = stringResource(R.string.model_provider_gigachat)) }
        )

        uiState.customModels.forEach { model ->
            Tab(
                selected = uiState.selectedModelId == model.id,
                onClick = { onModelSelect(model.id) },
                text = { Text(text = model.name.ifBlank { stringResource(R.string.model_settings_custom_unnamed) }) }
            )
        }

        Tab(
            selected = false,
            onClick = onAddCustomModel,
            text = { Text(text = stringResource(R.string.model_settings_add_model)) }
        )
    }
}
