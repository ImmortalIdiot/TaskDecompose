package io.ii.data.local.model

import io.ii.domain.model.CustomLlmSettings
import io.ii.domain.model.LlmSettings
import kotlinx.serialization.Serializable

/**
 * Локальная модель пользовательского LLM-профиля.
 *
 * Используется только для хранения списка пользовательских моделей в DataStore.
 *
 * @property id стабильный идентификатор профиля
 * @property name пользовательское название профиля
 * @property chatEndpoint эндпоинт chat/completions API
 * @property tokenEndpoint эндпоинт получения токена, если провайдер требует отдельную авторизацию
 * @property authToken статический ключ или токен авторизации
 * @property model идентификатор модели у провайдера
 * @property responseContentPath путь до текстового ответа модели в JSON-ответе
 * @property tokenPath путь до access token в JSON-ответе эндпоинта авторизации
 */
@Serializable
internal data class CustomLlm(
    val id: String,
    val name: String,
    val chatEndpoint: String = "",
    val tokenEndpoint: String = "",
    val authToken: String = "",
    val model: String = "",
    val responseContentPath: String = LlmSettings.DEFAULT_AI_API_PATH,
    val tokenPath: String = LlmSettings.DEFAULT_TOKEN_PATH
) {
    /**
     * Преобразует локальную модель в доменные настройки пользовательского профиля.
     *
     * @return доменная модель пользовательских настроек
     */
    fun toDomain(): CustomLlmSettings {
        return CustomLlmSettings(
            id = id,
            name = name,
            chatEndpoint = chatEndpoint,
            tokenEndpoint = tokenEndpoint,
            authToken = authToken,
            model = model,
            responseContentPath = responseContentPath,
            tokenPath = tokenPath
        )
    }

    companion object {
        /**
         * Создает локальную модель из доменных настроек пользовательского профиля.
         *
         * @param model доменная модель пользовательских настроек
         * @return локальная модель для сохранения в DataStore
         */
        fun fromDomain(model: CustomLlmSettings): CustomLlm {
            return CustomLlm(
                id = model.id,
                name = model.name,
                chatEndpoint = model.chatEndpoint,
                tokenEndpoint = model.tokenEndpoint,
                authToken = model.authToken,
                model = model.model,
                responseContentPath = model.responseContentPath,
                tokenPath = model.tokenPath
            )
        }
    }
}
