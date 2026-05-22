package io.ii.presentation.states

import io.ii.domain.model.LlmSettings

internal data class LlmSettingsUiState(
    val selectedModelId: String = LlmSettings.GIGACHAT_MODEL_ID,
    val gigaChatModel: String = LlmSettings.DEFAULT_GIGACHAT_MODEL,
    val customModels: List<CustomLlmUiState> = emptyList(),
    val isSaving: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
) {
    val selectedCustomModel: CustomLlmUiState?
        get() = customModels.firstOrNull { model -> model.id == selectedModelId }

    val isGigaChatSelected: Boolean
        get() = selectedModelId == LlmSettings.GIGACHAT_MODEL_ID

    val canSave: Boolean
        get() = !isSaving && if (isGigaChatSelected) {
            true
        } else {
            val model = selectedCustomModel

            model != null &&
                    model.name.isNotBlank() &&
                    model.chatEndpoint.isNotBlank() &&
                    model.responseContentPath.isNotBlank()
        }
}

internal data class CustomLlmUiState(
    val id: String,
    val name: String,
    val chatEndpoint: String = "",
    val tokenEndpoint: String = "",
    val authToken: String = "",
    val model: String = "",
    val responseContentPath: String = LlmSettings.DEFAULT_AI_API_PATH,
    val tokenPath: String = LlmSettings.DEFAULT_TOKEN_PATH
)
