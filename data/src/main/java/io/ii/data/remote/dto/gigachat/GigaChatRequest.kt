package io.ii.data.remote.dto.gigachat

import io.ii.data.remote.dto.common.ChatMessage
import kotlinx.serialization.Serializable

/**
 * DTO запроса к GigaChat API.
 *
 * Содержит выбранную модель и список сообщений. Cписок сообщений содержит только один промпт.
 *
 * @property messages список сообщений для модели
 * @property model идентификатор используемой модели
 */
@Serializable
internal data class GigaChatRequest(
    val messages: List<ChatMessage>,
    val model: String
)
