package io.ii.data.remote.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO статистики расхода токенов OpenAI-совместимого chat API.
 *
 * @property promptTokens токены пользовательского запроса
 * @property completionTokens токены ответа модели
 * @property totalTokens общий расход токенов на декомпозицию
 */
@Serializable
internal data class TokenUsage(
    @SerialName("prompt_tokens") val promptTokens: Long = 0L,
    @SerialName("completion_tokens") val completionTokens: Long = 0L,
    @SerialName("total_tokens") val totalTokens: Long = 0L
)
