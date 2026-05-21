package io.ii.data.remote.api.custom

import android.os.SystemClock
import io.ii.data.remote.dto.custom.OpenAiCompatibleRequest
import io.ii.data.remote.dto.custom.OpenAiCompatibleResponse
import io.ii.data.model.JsonContentExtractor
import io.ii.data.model.LlmClient
import io.ii.data.remote.dto.common.DecompositionApiResult
import io.ii.data.remote.dto.common.ChatMessage
import io.ii.data.utils.Constants
import io.ii.domain.model.CustomLlmSettings
import io.ii.domain.model.LlmSettings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Клиент пользовательского OpenAI-совместимого API модели.
 *
 * Использует сохранённые пользователем эндпоинты, токен авторизации и пути в JSON-ответе,
 * чтобы отправить промпт и извлечь текст ответа модели.
 */
internal class CustomModelApi(
    private val client: HttpClient,
    private val settings: CustomLlmSettings
) : LlmClient {

    /**
     * Выполняет декомпозицию через пользовательскую модель.
     *
     * Отправляет OpenAI-совместимый запрос на пользовательский chat endpoint,
     * извлекает текст ответа по сохранённому пути и парсит его как список подзадач.
     *
     * @param prompt промпт для модели
     * @return результат декомпозиции
     */
    override suspend fun decomposeTask(prompt: String): DecompositionApiResult {
        val chatEndpoint = settings.chatEndpoint.trim()
        require(chatEndpoint.isNotBlank()) { "Custom model endpoint is empty" }

        val accessToken = resolveAccessToken()
        val requestStartedAt = SystemClock.elapsedRealtime()
        val response: JsonElement = client.post(chatEndpoint) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            if (accessToken.isNotBlank()) {
                bearerAuth(accessToken)
            }
            setBody(
                OpenAiCompatibleRequest(
                    model = settings.model.trim().ifBlank { null },
                    messages = listOf(
                        ChatMessage(
                            role = Constants.USER_ROLE,
                            content = prompt
                        )
                    )
                )
            )
        }.body()

        val content = JsonContentExtractor.extractString(
            root = response,
            path = settings.responseContentPath.ifBlank {
                LlmSettings.Companion.DEFAULT_AI_API_PATH
            }
        )

        val usage = runCatching {
            Json.Default.decodeFromJsonElement(
                deserializer = OpenAiCompatibleResponse.serializer(),
                element = response
            ).usage
        }.getOrNull()

        return DecompositionApiResult(
            tasks = Json.Default.decodeFromString(content),
            usage = usage,
            requestResponseDurationMillis = SystemClock.elapsedRealtime() - requestStartedAt
        )
    }

    /**
     * Возвращает токен авторизации для пользовательского API.
     *
     * Если отдельный token endpoint не задан, используется статический токен из настроек.
     * Если endpoint задан, выполняется запрос и токен извлекается из ответа по сохранённому пути.
     *
     * @return токен авторизации или пустая строка, если авторизация не нужна
     */
    private suspend fun resolveAccessToken(): String {
        val staticToken = settings.authToken.trim()
        val tokenEndpoint = settings.tokenEndpoint.trim()

        if (tokenEndpoint.isBlank()) {
            return staticToken
        }

        val response: JsonElement = client.post(tokenEndpoint) {
            accept(ContentType.Application.Json)
            if (staticToken.isNotBlank()) {
                bearerAuth(staticToken)
            }
        }.body()

        return JsonContentExtractor.extractString(
            root = response,
            path = settings.tokenPath.ifBlank {
                LlmSettings.Companion.DEFAULT_TOKEN_PATH
            }
        )
    }
}
