package io.ii.data.local.model

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.ii.domain.model.CustomLlmSettings
import io.ii.domain.model.LlmSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Локальное хранилище настроек языковой модели.
 *
 * Использует DataStore Preferences для сохранения выбранной модели,
 * настроек GigaChat и списка пользовательских моделей.
 */
internal class LlmSettingsStorage(
    private val context: Context
) {
    /**
     * Возвращает поток сохранённых настроек модели.
     *
     * @return поток настроек модели
     */
    fun observeSettings(): Flow<LlmSettings> {
        return context.dataStore.data.map { preferences ->
            val customModels = preferences[CUSTOM_MODELS]?.let(::decodeCustomModels)
                .orEmpty()
                .ifEmpty {
                    createLegacyCustomModel(
                        chatEndpoint = preferences[LEGACY_CUSTOM_CHAT_ENDPOINT].orEmpty(),
                        tokenEndpoint = preferences[LEGACY_CUSTOM_TOKEN_ENDPOINT].orEmpty(),
                        authToken = preferences[LEGACY_CUSTOM_AUTH_TOKEN].orEmpty(),
                        model = preferences[LEGACY_CUSTOM_MODEL].orEmpty(),
                        responseContentPath = preferences[LEGACY_CUSTOM_RESPONSE_CONTENT_PATH] ?: LlmSettings.DEFAULT_AI_API_PATH,
                        tokenPath = preferences[LEGACY_CUSTOM_TOKEN_PATH] ?: LlmSettings.DEFAULT_TOKEN_PATH
                    )
                }

            LlmSettings(
                selectedModelId = preferences[SELECTED_MODEL_ID]
                    ?: resolveLegacySelectedModelId(
                        legacyProvider = preferences[LEGACY_PROVIDER_KEY],
                        customModels = customModels
                    ),
                gigaChatModel = preferences[GIGACHAT_MODEL] ?: LlmSettings.DEFAULT_GIGACHAT_MODEL,
                customModels = customModels
            )
        }
    }

    /**
     * Возвращает текущие настройки модели.
     *
     * @return актуальные настройки модели
     */
    suspend fun getSettings(): LlmSettings = observeSettings().first()

    /**
     * Сохраняет настройки модели в DataStore.
     *
     * @param settings настройки модели
     */
    suspend fun saveSettings(settings: LlmSettings) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_MODEL_ID] = settings.selectedModelId
            preferences[GIGACHAT_MODEL] = settings.gigaChatModel
            preferences[CUSTOM_MODELS] = json.encodeToString(
                settings.customModels.map(CustomLlm::fromDomain)
            )
        }
    }

    private fun decodeCustomModels(rawValue: String): List<CustomLlmSettings> {
        return runCatching {
            json.decodeFromString<List<CustomLlm>>(rawValue)
                .map(CustomLlm::toDomain)
        }.getOrDefault(emptyList())
    }

    private fun createLegacyCustomModel(
        chatEndpoint: String,
        tokenEndpoint: String,
        authToken: String,
        model: String,
        responseContentPath: String,
        tokenPath: String
    ): List<CustomLlmSettings> {
        if (chatEndpoint.isBlank() && authToken.isBlank() && model.isBlank()) {
            return emptyList()
        }

        return listOf(
            CustomLlmSettings(
                id = LEGACY_CUSTOM_MODEL_ID,
                name = model.ifBlank { LEGACY_CUSTOM_MODEL_NAME },
                chatEndpoint = chatEndpoint,
                tokenEndpoint = tokenEndpoint,
                authToken = authToken,
                model = model,
                responseContentPath = responseContentPath,
                tokenPath = tokenPath
            )
        )
    }

    private fun resolveLegacySelectedModelId(
        legacyProvider: String?,
        customModels: List<CustomLlmSettings>
    ): String {
        return if (legacyProvider == LEGACY_CUSTOM_PROVIDER && customModels.isNotEmpty()) {
            customModels.first().id
        } else {
            LlmSettings.GIGACHAT_MODEL_ID
        }
    }

    private companion object {
        private const val PREFERENCES_NAME = "language_model_settings"
        private const val LEGACY_CUSTOM_PROVIDER = "CUSTOM"
        private const val LEGACY_CUSTOM_MODEL_ID = "custom_legacy"
        private const val LEGACY_CUSTOM_MODEL_NAME = "Своя модель"

        private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)

        private val SELECTED_MODEL_ID = stringPreferencesKey("selected_model_id")
        private val GIGACHAT_MODEL = stringPreferencesKey("gigachat_model")
        private val CUSTOM_MODELS = stringPreferencesKey("custom_models")

        /**
         * Ключи прежней схемы с одной пользовательской моделью.
         *
         * Используются для переноса уже сохранённых настроек в новый список CustomLlm.
         */
        private val LEGACY_PROVIDER_KEY = stringPreferencesKey("provider")
        private val LEGACY_CUSTOM_CHAT_ENDPOINT = stringPreferencesKey("custom_chat_endpoint")
        private val LEGACY_CUSTOM_TOKEN_ENDPOINT = stringPreferencesKey("custom_token_endpoint")
        private val LEGACY_CUSTOM_AUTH_TOKEN = stringPreferencesKey("custom_auth_token")
        private val LEGACY_CUSTOM_MODEL = stringPreferencesKey("custom_model")
        private val LEGACY_CUSTOM_RESPONSE_CONTENT_PATH = stringPreferencesKey("custom_response_content_path")
        private val LEGACY_CUSTOM_TOKEN_PATH = stringPreferencesKey("custom_token_path")

        private val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
}
