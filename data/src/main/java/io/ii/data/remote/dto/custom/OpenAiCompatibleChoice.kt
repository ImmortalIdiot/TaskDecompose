package io.ii.data.remote.dto.custom

import io.ii.data.remote.dto.common.ChatMessage
import kotlinx.serialization.Serializable

/**
 * DTO одного варианта ответа OpenAI-совместимой модели.
 *
 * @property message финальное сообщение модели
 * @property delta фрагмент потокового ответа, если используется streaming
 */
@Serializable
internal data class OpenAiCompatibleChoice(
    val message: ChatMessage? = null,
    val delta: ChatMessage? = null
)
