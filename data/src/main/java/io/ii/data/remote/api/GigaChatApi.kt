package io.ii.data.remote.api

import io.ii.data.BuildConfig
import io.ii.data.remote.dto.GigaChatAccessToken
import io.ii.data.remote.dto.GigaChatMessage
import io.ii.data.remote.dto.GigaChatRequest
import io.ii.data.remote.dto.GigaChatResponse
import io.ii.data.remote.dto.SubtaskDto
import io.ii.data.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import kotlinx.serialization.json.Json
import java.util.UUID

internal class GigaChatApi(
    private val client: HttpClient
) {

    /**
     * Отправляет промпт в GigaChat API и возвращает список сгенерированных подзадач.
     *
     * Выполняет запрос к эндпоинту chat/completions, получает текстовый ответ модели
     * и преобразует содержимое ответа в список [SubtaskDto].
     *
     * Ожидается, что модель вернёт JSON-массив подзадач в поле message.content.
     *
     * @param token токен доступа для авторизации запроса
     * @param prompt текст запроса для модели
     * @return список подзадач, полученных от модели
     */
    suspend fun decomposeTask(token: String, prompt: String): List<SubtaskDto> {

        val response: GigaChatResponse = client.post(BuildConfig.BASE_URL) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)

            bearerAuth(token)

            setBody(
                GigaChatRequest(
                    messages = listOf(
                        GigaChatMessage(
                            role = Constants.USER_ROLE,
                            content = prompt
                        )
                    )
                )
            )
        }.body()

        return Json.decodeFromString(
            string = response.choices.first().message.content
        )
    }

    /**
     * Выполняет авторизацию в GigaChat API.
     *
     * Отправляет запрос на эндпоинт авторизации и получает токен доступа, используемый для выполнения последующих запросов к API.
     *
     * В запросе автоматически генерируется уникальный идентификатор запроса [Constants.REQUEST_ID_HEADER] в формате UUID.
     *
     * @return DTO с токеном доступа и временем его истечения
     */
    suspend fun authorize(): GigaChatAccessToken {
        return client.post(BuildConfig.AUTH_URL) {
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)

            header(
                key = Constants.REQUEST_ID_HEADER,
                value = UUID.randomUUID().toString()
            )

            header(
                key = HttpHeaders.Authorization,
                value = "Basic: ${BuildConfig.AUTH_TOKEN}"
            )

            setBody(
                Parameters.build {
                    append(
                        name = Constants.AUTH_BODY_SCOPE_KEY,
                        value = Constants.AUTH_BODY_SCOPE_VALUE
                    )
                }.formUrlEncode()
            )
        }.body()
    }
}
