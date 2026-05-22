package io.ii.presentation.utils

import io.ii.domain.model.CustomLlmSettings
import io.ii.domain.model.LlmSettings
import io.ii.presentation.states.CustomLlmUiState
import io.ii.presentation.states.LlmSettingsUiState

/**
 * Преобразует доменные настройки модели в UI state экрана настроек.
 *
 * @return состояние экрана настроек модели
 */
internal fun LlmSettings.toUiState(): LlmSettingsUiState =
    LlmSettingsUiState(
        selectedModelId = selectedModelId,
        gigaChatModel = gigaChatModel,
        customModels = customModels.map(CustomLlmSettings::toUiState)
    )

/**
 * Преобразует UI state экрана настроек в доменную модель.
 *
 * Перед сохранением удаляет лишние пробелы в строковых настройках.
 *
 * @return доменная модель настроек модели
 */
internal fun LlmSettingsUiState.toDomain(): LlmSettings =
    LlmSettings(
        selectedModelId = selectedModelId,
        gigaChatModel = gigaChatModel.trim(),
        customModels = customModels.map(CustomLlmUiState::toDomain)
    )

private fun CustomLlmSettings.toUiState(): CustomLlmUiState =
    CustomLlmUiState(
        id = id,
        name = name,
        chatEndpoint = chatEndpoint,
        tokenEndpoint = tokenEndpoint,
        authToken = authToken,
        model = model,
        responseContentPath = responseContentPath,
        tokenPath = tokenPath
    )

private fun CustomLlmUiState.toDomain(): CustomLlmSettings =
    CustomLlmSettings(
        id = id,
        name = name.trim(),
        chatEndpoint = chatEndpoint.trim(),
        tokenEndpoint = tokenEndpoint.trim(),
        authToken = authToken.trim(),
        model = model.trim(),
        responseContentPath = responseContentPath.trim(),
        tokenPath = tokenPath.trim()
    )
