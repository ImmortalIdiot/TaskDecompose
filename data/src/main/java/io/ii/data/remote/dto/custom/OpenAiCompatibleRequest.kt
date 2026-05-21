package io.ii.data.remote.dto.custom

import io.ii.data.remote.dto.common.ChatMessage
import kotlinx.serialization.Serializable

/**
 * DTO OpenAI-совместимого запроса к chat/completions API.
 *
 * Используется для пользовательских API, поддерживающих совместимый формат.
 */
@Serializable
internal data class OpenAiCompatibleRequest(
    val model: String? = null,
    val messages: List<ChatMessage>
)
