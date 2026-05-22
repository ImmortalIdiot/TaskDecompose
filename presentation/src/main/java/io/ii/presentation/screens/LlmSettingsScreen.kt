package io.ii.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.ii.presentation.R
import io.ii.presentation.components.bars.LlmSettingsTopBar
import io.ii.presentation.components.bars.TaskEditSnackbar
import io.ii.presentation.components.cards.CustomSettingsCard
import io.ii.presentation.components.cards.GigaChatSettingsCard
import io.ii.presentation.components.other.LlmTabs
import io.ii.presentation.theme.LocalDimensions
import io.ii.presentation.utils.Constants
import io.ii.presentation.viewmodels.LlmSettingsViewModel
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LlmSettingsScreen(
    viewModel: LlmSettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dimensions = LocalDimensions.current
    val snackbarMessage = uiState.errorMessage ?: uiState.successMessage

    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        if (snackbarMessage != null) {
            delay(Constants.SETTINGS_SNACKBAR_DURATION)
            viewModel.clearMessages()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            LlmSettingsTopBar(
                canDelete = !uiState.isGigaChatSelected,
                canSave = uiState.canSave,
                onDeleteClick = viewModel::deleteSelectedCustomModel,
                onSaveClick = viewModel::saveSettings
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(dimensions.padding.paddingM),
                verticalArrangement = Arrangement.spacedBy(dimensions.padding.paddingM)
            ) {
                item {
                    LlmTabs(
                        uiState = uiState,
                        onModelSelect = viewModel::onModelSelect,
                        onAddCustomModel = viewModel::addCustomModel
                    )
                }

                if (uiState.isGigaChatSelected) {
                    item {
                        GigaChatSettingsCard(
                            uiState = uiState,
                            onModelChange = viewModel::onGigaChatModelChange
                        )
                    }

                    item {
                        Text(
                            text = stringResource(R.string.model_settings_gigachat_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    uiState.selectedCustomModel?.let { model ->
                        item {
                            CustomSettingsCard(
                                model = model,
                                onNameChange = viewModel::onCustomNameChange,
                                onChatEndpointChange = viewModel::onCustomChatEndpointChange,
                                onTokenEndpointChange = viewModel::onCustomTokenEndpointChange,
                                onAuthTokenChange = viewModel::onCustomAuthTokenChange,
                                onModelChange = viewModel::onCustomModelChange,
                                onResponseContentPathChange = viewModel::onCustomResponseContentPathChange,
                                onTokenPathChange = viewModel::onCustomTokenPathChange
                            )
                        }
                    }
                }
            }
        }

        TaskEditSnackbar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(dimensions.padding.paddingM),
            message = snackbarMessage,
            isError = uiState.errorMessage != null,
            onDismiss = viewModel::clearMessages
        )
    }
}
