package io.ii.data.remote.dto.gigachat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO токена доступа GigaChat API.
 *
 * Содержит токен авторизации и время его истечения, полученные в ответе от эндпоинта авторизации.
 *
 * @property accessToken токен доступа для авторизации запросов к API
 * @property expiresAt время истечения токена в формате timestamp
 */
@Serializable
internal data class GigaChatAccessToken(
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_at") val expiresAt: Long
)
