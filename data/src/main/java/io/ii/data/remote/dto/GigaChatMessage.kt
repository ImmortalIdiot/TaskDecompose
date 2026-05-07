package io.ii.data.remote.dto

import io.ii.data.utils.Constants
import kotlinx.serialization.Serializable

/**
 * DTO сообщения для GigaChat API.
 *
 * Используется для отправки пользовательского сообщения и чтения сообщения модели из ответа API.
 *
 * @property content текст сообщения
 * @property role роль автора сообщения
 */
@Serializable
internal data class GigaChatMessage(
    val content: String,
    val role: String = Constants.USER_ROLE
)
