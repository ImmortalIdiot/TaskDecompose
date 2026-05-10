package io.ii.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
internal sealed class Route(val index: Int) {

    @Serializable
    data class TaskEditor(val taskId: String? = null) : Route(TASK_EDITOR_INDEX)

    @Serializable
    data object History : Route(HISTORY_INDEX)

    companion object {
        val ALL = listOf(TaskEditor(), History)

        private const val TASK_EDITOR_INDEX = 0
        private const val HISTORY_INDEX = 1
    }
}
