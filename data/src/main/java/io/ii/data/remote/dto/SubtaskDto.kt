package io.ii.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class SubtaskDto(
    val title: String,
    val subtasks: List<SubtaskDto> = emptyList()
)
