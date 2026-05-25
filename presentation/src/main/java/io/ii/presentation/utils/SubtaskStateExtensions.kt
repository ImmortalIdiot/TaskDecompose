package io.ii.presentation.utils

import io.ii.presentation.states.SubtaskState

internal fun List<SubtaskState>.setAllCompleted(isCompleted: Boolean): List<SubtaskState> =
    map { subtask ->
        subtask.copy(
            isCompleted = isCompleted,
            subtasks = subtask.subtasks.setAllCompleted(isCompleted)
        )
    }

internal fun List<SubtaskState>.updateCompletedCascade(
    id: String,
    isCompleted: Boolean
): List<SubtaskState> =
    map { subtask ->
        if (subtask.id == id) {
            subtask.copy(
                isCompleted = isCompleted,
                subtasks = subtask.subtasks.setAllCompleted(isCompleted)
            )
        } else {
            subtask.copy(
                subtasks = subtask.subtasks.updateCompletedCascade(
                    id = id,
                    isCompleted = isCompleted
                )
            )
        }
    }
