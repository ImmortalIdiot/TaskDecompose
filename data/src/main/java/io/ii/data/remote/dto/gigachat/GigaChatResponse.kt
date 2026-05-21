package io.ii.data.remote.dto.gigachat

import io.ii.data.remote.dto.common.ChatMessage
import io.ii.data.remote.dto.common.TokenUsage
import kotlinx.serialization.Serializable

/**
 * DTO ответа GigaChat API.
 *
 * @property choices список вариантов ответа модели
 * @property usage статистика расхода токенов
 */
@Serializable
internal data class GigaChatResponse(
    val choices: List<GigaChatChoice>,
    val usage: TokenUsage? = null
)

/**
 * DTO одного варианта ответа GigaChat API.
 *
 * @property message сообщение, сгенерированное моделью
 */
@Serializable
internal data class GigaChatChoice(
    val message: ChatMessage
)
