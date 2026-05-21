package io.ii.data.remote.api.gigachat

import android.os.SystemClock
import io.ii.data.BuildConfig
import io.ii.data.local.token.AccessTokenStorage
import io.ii.data.model.LlmClient
import io.ii.data.remote.dto.common.DecompositionApiResult
import io.ii.data.remote.dto.gigachat.GigaChatAccessToken
import io.ii.data.remote.dto.common.ChatMessage
import io.ii.data.remote.dto.gigachat.GigaChatRequest
import io.ii.data.remote.dto.gigachat.GigaChatResponse
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
    private val client: HttpClient,
    private val tokenStorage: AccessTokenStorage
) : LlmClient {

    override suspend fun decomposeTask(prompt: String): DecompositionApiResult {
        return decomposeTask(
            prompt = prompt,
            model = Constants.GIGACHAT_MODEL
        )
    }

    suspend fun decomposeTask(
        prompt: String,
        model: String
    ): DecompositionApiResult {
        val token = getValidAccessToken()
        val requestStartedAt = SystemClock.elapsedRealtime()

        val response: GigaChatResponse = client.post(BuildConfig.BASE_URL) {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
            bearerAuth(token.accessToken)
            setBody(
                GigaChatRequest(
                    messages = listOf(
                        ChatMessage(
                            role = Constants.USER_ROLE,
                            content = prompt
                        )
                    ),
                    model = model.ifBlank { Constants.GIGACHAT_MODEL }
                )
            )
        }.body()

        return DecompositionApiResult(
            tasks = Json.Default.decodeFromString(response.choices.first().message.content),
            usage = response.usage,
            requestResponseDurationMillis = SystemClock.elapsedRealtime() - requestStartedAt
        )
    }

    private suspend fun authorize(): GigaChatAccessToken {
        val token = client.post(BuildConfig.AUTH_URL) {
            contentType(ContentType.Application.FormUrlEncoded)
            accept(ContentType.Application.Json)
            header(
                key = Constants.REQUEST_ID_HEADER,
                value = UUID.randomUUID().toString()
            )
            header(
                key = HttpHeaders.Authorization,
                value = "Basic ${BuildConfig.AUTH_TOKEN}"
            )
            setBody(
                Parameters.Companion.build {
                    append(
                        name = Constants.AUTH_BODY_SCOPE_KEY,
                        value = Constants.AUTH_BODY_SCOPE_VALUE
                    )
                }.formUrlEncode()
            )
        }.body<GigaChatAccessToken>()

        tokenStorage.saveToken(token)
        return token
    }

    private suspend fun getValidAccessToken(): GigaChatAccessToken {
        val savedToken = tokenStorage.getToken()

        return if (savedToken != null && savedToken.isValid()) {
            savedToken
        } else {
            authorize()
        }
    }

    private fun GigaChatAccessToken.isValid(): Boolean {
        return accessToken.isNotBlank() &&
                expiresAt > System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_SAFETY_TIMEOUT_MILLIS
    }
}
