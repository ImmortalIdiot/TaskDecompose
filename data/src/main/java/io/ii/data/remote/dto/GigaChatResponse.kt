package io.ii.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO ответа GigaChat API.
 *
 * @property choices список вариантов ответа модели
 */
@Serializable
internal data class GigaChatResponse(
    val choices: List<GigaChatChoice>
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
