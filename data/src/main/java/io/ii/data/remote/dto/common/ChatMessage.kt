package io.ii.data.remote.dto.common

import kotlinx.serialization.Serializable

/**
 * DTO сообщения для OpenAI-совместимого chat API.
 *
 * Используется для отправки пользовательского сообщения и чтения сообщения модели из ответа API.
 *
 * @property content текст сообщения
 * @property role роль автора сообщения
 */
@Serializable
internal data class ChatMessage(
    val content: String,
    val role: String
)
