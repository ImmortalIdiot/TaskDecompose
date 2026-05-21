package io.ii.presentation.components.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.ii.presentation.R
import io.ii.presentation.components.inputs.SettingsTextField
import io.ii.presentation.states.CustomLlmUiState
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.theme.TaskDecomposeComponentDefaults

@Composable
internal fun CustomSettingsCard(
    model: CustomLlmUiState,
    onNameChange: (String) -> Unit,
    onChatEndpointChange: (String) -> Unit,
    onTokenEndpointChange: (String) -> Unit,
    onAuthTokenChange: (String) -> Unit,
    onModelChange: (String) -> Unit,
    onResponseContentPathChange: (String) -> Unit,
    onTokenPathChange: (String) -> Unit,
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
            SettingsTextField(
                value = model.name,
                onValueChange = onNameChange,
                label = stringResource(R.string.model_settings_custom_name)
            )
            SettingsTextField(
                value = model.chatEndpoint,
                onValueChange = onChatEndpointChange,
                label = stringResource(R.string.model_settings_chat_endpoint)
            )
            SettingsTextField(
                value = model.tokenEndpoint,
                onValueChange = onTokenEndpointChange,
                label = stringResource(R.string.model_settings_token_endpoint)
            )
            SettingsTextField(
                value = model.authToken,
                onValueChange = onAuthTokenChange,
                label = stringResource(R.string.model_settings_auth_token)
            )
            SettingsTextField(
                value = model.model,
                onValueChange = onModelChange,
                label = stringResource(R.string.model_settings_model_optional)
            )
            SettingsTextField(
                value = model.responseContentPath,
                onValueChange = onResponseContentPathChange,
                label = stringResource(R.string.model_settings_response_path)
            )
            SettingsTextField(
                value = model.tokenPath,
                onValueChange = onTokenPathChange,
                label = stringResource(R.string.model_settings_token_path)
            )
        }
    }
}
