package io.ii.data.remote.dto.custom

import io.ii.data.remote.dto.common.TokenUsage
import kotlinx.serialization.Serializable

/**
 * DTO OpenAI-совместимого ответа chat/completions.
 *
 * @property choices варианты ответа модели
 * @property usage статистика расхода токенов, если провайдер её вернул
 */
@Serializable
internal data class OpenAiCompatibleResponse(
    val choices: List<OpenAiCompatibleChoice> = emptyList(),
    val usage: TokenUsage? = null
)
