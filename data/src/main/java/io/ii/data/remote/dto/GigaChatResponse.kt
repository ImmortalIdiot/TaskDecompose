package io.ii.data.remote.dto

import kotlinx.serialization.SerialName
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
    val usage: GigaChatUsage? = null
)

/**
 * DTO одного варианта ответа GigaChat API.
 *
 * @property message сообщение, сгенерированное моделью
 */
@Serializable
internal data class GigaChatChoice(
    val message: GigaChatMessage
)

/**
 * DTO статистики расхода токенов GigaChat API.
 *
 * @property promptTokens токены пользовательского запроса
 * @property completionTokens токены ответа модели
 * @property totalTokens общий расход токенов на декомпозицию
 */
@Serializable
internal data class GigaChatUsage(
    @SerialName("prompt_tokens") val promptTokens: Long = 0L,
    @SerialName("completion_tokens") val completionTokens: Long = 0L,
    @SerialName("total_tokens") val totalTokens: Long = 0L
)
