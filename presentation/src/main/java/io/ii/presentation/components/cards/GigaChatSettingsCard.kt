package io.ii.presentation.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.ii.domain.model.LlmSettings
import io.ii.presentation.R
import io.ii.presentation.components.inputs.ModelDropdown
import io.ii.presentation.states.LlmSettingsUiState
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.theme.TaskDecomposeComponentDefaults

@Composable
internal fun GigaChatSettingsCard(
    uiState: LlmSettingsUiState,
    onModelChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = LocalDimensions.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = TaskDecomposeComponentDefaults.cardColors()
    ) {
        Column(
            modifier = Modifier.padding(dimensions.padding.paddingM),
            verticalArrangement = Arrangement.spacedBy(dimensions.padding.padding12)
        ) {
            ModelDropdown(
                value = uiState.gigaChatModel,
                values = LlmSettings.GIGACHAT_MODELS,
                onValueChange = onModelChange,
                label = stringResource(R.string.model_settings_model)
            )
        }
    }
}
