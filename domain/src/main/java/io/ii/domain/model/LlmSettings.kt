package io.ii.domain.model

/**
 * Настройки языковых моделей.
 *
 * Содержит выбранную модель, настройки GigaChat и список пользовательских
 * OpenAI-совместимых моделей.
 *
 * @property selectedModelId идентификатор выбранной модели
 * @property gigaChatModel идентификатор модели GigaChat
 * @property customModels пользовательские модели
 */
data class LlmSettings(
    val selectedModelId: String = GIGACHAT_MODEL_ID,
    val gigaChatModel: String = DEFAULT_GIGACHAT_MODEL,
    val customModels: List<CustomLlmSettings> = emptyList()
) {
    companion object {
        const val GIGACHAT_MODEL_ID = "gigachat"
        const val DEFAULT_GIGACHAT_MODEL = "GigaChat"
        const val DEFAULT_AI_API_PATH = "choices.0.message.content"
        const val DEFAULT_TOKEN_PATH = "access_token"

        val GIGACHAT_MODELS = listOf(
            "GigaChat",
            "GigaChat-Pro",
            "GigaChat-Max"
        )
    }
}

/**
 * Настройки пользовательской OpenAI-совместимой модели.
 *
 * @property id стабильный идентификатор модели
 * @property name пользовательское название модели
 * @property chatEndpoint эндпоинт пользовательского chat/completions API
 * @property tokenEndpoint эндпоинт получения токена пользовательского API, если требуется
 * @property authToken статический ключ или токен авторизации пользовательского API
 * @property model идентификатор модели у провайдера
 * @property responseContentPath путь до текстового ответа модели в JSON-ответе
 * @property tokenPath путь до access token в JSON-ответе эндпоинта авторизации
 */
data class CustomLlmSettings(
    val id: String,
    val name: String,
    val chatEndpoint: String = "",
    val tokenEndpoint: String = "",
    val authToken: String = "",
    val model: String = "",
    val responseContentPath: String = LlmSettings.DEFAULT_AI_API_PATH,
    val tokenPath: String = LlmSettings.DEFAULT_TOKEN_PATH
)
