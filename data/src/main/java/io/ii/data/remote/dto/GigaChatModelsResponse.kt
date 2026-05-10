package io.ii.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO ответа GigaChat API со списком доступных моделей.
 *
 * @property data список моделей, доступных текущему токену
 */
@Serializable
internal data class GigaChatModelsResponse(
    val data: List<GigaChatModel>
)

/**
 * DTO модели GigaChat.
 *
 * @property id идентификатор модели
 * @property ownedBy владелец модели
 * @property type тип модели
 */
@Serializable
internal data class GigaChatModel(
    val id: String,
    @SerialName("owned_by") val ownedBy: String? = null,
    val type: String? = null
)
