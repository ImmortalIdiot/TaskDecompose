package io.ii.data.remote.dto.custom

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO ответа пользовательского эндпоинта авторизации.
 *
 * @property accessToken токен доступа
 * @property expiresAt время истечения токена в формате timestamp
 * @property expiresInSeconds время жизни токена в секундах
 */
@Serializable
internal data class CustomAccessToken(
    @SerialName("access_token") val accessToken: String? = null,
    @SerialName("expires_at") val expiresAt: Long? = null,
    @SerialName("expires_in") val expiresInSeconds: Long? = null
)
