package io.ii.presentation.viewmodels

import androidx.lifecycle.ViewModel
import io.ii.domain.model.LlmSettings
import io.ii.domain.usecase.ObserveLlmSettingsUseCase
import io.ii.domain.usecase.SaveLlmSettingsUseCase
import io.ii.presentation.R
import io.ii.presentation.core.ResourceProvider
import io.ii.presentation.core.launchSafe
import io.ii.presentation.states.CustomLlmUiState
import io.ii.presentation.states.LlmSettingsUiState
import io.ii.presentation.utils.toDomain
import io.ii.presentation.utils.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import java.util.UUID

/**
 * ViewModel экрана настроек языковой модели.
 *
 * Отвечает за загрузку сохранённых настроек, обработку изменений формы,
 * добавление пользовательских моделей и сохранение выбранной модели.
 */
internal class LlmSettingsViewModel(
    private val observeSettingsUseCase: ObserveLlmSettingsUseCase,
    private val saveSettingsUseCase: SaveLlmSettingsUseCase,
    private val resourceProvider: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(LlmSettingsUiState())
    val uiState: StateFlow<LlmSettingsUiState> = _uiState.asStateFlow()

    init {
        launchSafe {
            observeSettingsUseCase().collectLatest { settings ->
                val current = _uiState.value
                _uiState.value = settings.toUiState().copy(
                    successMessage = current.successMessage,
                    errorMessage = current.errorMessage
                )
            }
        }
    }

    /**
     * Обновляет выбранную модель.
     *
     * @param modelId идентификатор модели
     */
    fun onModelSelect(modelId: String) {
        _uiState.update { state -> state.copy(selectedModelId = modelId) }
    }

    /**
     * Добавляет новую пользовательскую модель и выбирает её.
     */
    fun addCustomModel() {
        val modelId = UUID.randomUUID().toString()

        _uiState.update { state ->
            state.copy(
                selectedModelId = modelId,
                customModels = state.customModels + CustomLlmUiState(
                    id = modelId,
                    name = DEFAULT_CUSTOM_MODEL_NAME
                )
            )
        }
    }

    /**
     * Удаляет выбранную пользовательскую модель.
     *
     * После удаления выбирает следующую пользовательскую модель или GigaChat,
     * если пользовательских моделей больше нет.
     */
    fun deleteSelectedCustomModel() {
        _uiState.update { state ->
            val deletingModelIndex = state.customModels.indexOfFirst { model ->
                model.id == state.selectedModelId
            }

            if (deletingModelIndex < 0) {
                return@update state
            }

            val updatedModels = state.customModels.filterNot { model ->
                model.id == state.selectedModelId
            }
            val nextSelectedModelId = updatedModels
                .getOrNull(deletingModelIndex)
                ?.id
                ?: updatedModels
                    .getOrNull(deletingModelIndex - 1)
                    ?.id
                ?: LlmSettings.GIGACHAT_MODEL_ID

            state.copy(
                selectedModelId = nextSelectedModelId,
                customModels = updatedModels
            )
        }
    }

    /**
     * Обновляет выбранную модель GigaChat.
     *
     * @param value идентификатор модели
     */
    fun onGigaChatModelChange(value: String) {
        _uiState.update { state -> state.copy(gigaChatModel = value) }
    }

    /**
     * Обновляет пользовательское название выбранной модели.
     *
     * @param value название модели
     */
    fun onCustomNameChange(value: String) {
        updateSelectedCustomModel { model -> model.copy(name = value) }
    }

    /**
     * Обновляет chat endpoint пользовательской модели.
     *
     * @param value URL endpoint
     */
    fun onCustomChatEndpointChange(value: String) {
        updateSelectedCustomModel { model -> model.copy(chatEndpoint = value) }
    }

    /**
     * Обновляет endpoint получения токена пользовательской модели.
     *
     * @param value URL token endpoint
     */
    fun onCustomTokenEndpointChange(value: String) {
        updateSelectedCustomModel { model -> model.copy(tokenEndpoint = value) }
    }

    /**
     * Обновляет статический ключ или токен пользовательской модели.
     *
     * @param value ключ или токен авторизации
     */
    fun onCustomAuthTokenChange(value: String) {
        updateSelectedCustomModel { model -> model.copy(authToken = value) }
    }

    /**
     * Обновляет идентификатор пользовательской модели.
     *
     * @param value идентификатор модели
     */
    fun onCustomModelChange(value: String) {
        updateSelectedCustomModel { model -> model.copy(model = value) }
    }

    /**
     * Обновляет путь до текстового ответа модели в JSON.
     *
     * @param value путь до поля с текстом ответа
     */
    fun onCustomResponseContentPathChange(value: String) {
        updateSelectedCustomModel { model -> model.copy(responseContentPath = value) }
    }

    /**
     * Обновляет путь до токена в JSON-ответе token endpoint.
     *
     * @param value путь до поля с токеном
     */
    fun onCustomTokenPathChange(value: String) {
        updateSelectedCustomModel { model -> model.copy(tokenPath = value) }
    }

    /**
     * Сохраняет текущие настройки модели.
     */
    fun saveSettings() {
        launchSafe(
            start = {
                _uiState.update { state ->
                    state.copy(isSaving = true, errorMessage = null, successMessage = null)
                }
            },
            onError = { error ->
                _uiState.update { state ->
                    state.copy(errorMessage = error.localizedMessage.orEmpty())
                }
            },
            final = {
                _uiState.update { state -> state.copy(isSaving = false) }
            }
        ) {
            saveSettingsUseCase(_uiState.value.toDomain())
            _uiState.update { state ->
                state.copy(
                    successMessage = resourceProvider.getString(R.string.model_settings_saved)
                )
            }
        }
    }

    /**
     * Очищает сообщения об ошибке и успешном сохранении.
     */
    fun clearMessages() {
        _uiState.update { state -> state.copy(successMessage = null, errorMessage = null) }
    }

    private fun updateSelectedCustomModel(
        transform: (CustomLlmUiState) -> CustomLlmUiState
    ) {
        _uiState.update { state ->
            state.copy(
                customModels = state.customModels.map { model ->
                    if (model.id == state.selectedModelId) {
                        transform(model)
                    } else {
                        model
                    }
                }
            )
        }
    }

    private companion object {
        const val DEFAULT_CUSTOM_MODEL_NAME = "Новая модель"
    }
}
