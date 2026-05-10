package io.ii.data.remote.dto

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
    val role: String
)
