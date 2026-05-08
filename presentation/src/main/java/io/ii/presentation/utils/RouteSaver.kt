package io.ii.presentation.utils

import androidx.compose.runtime.saveable.Saver
import io.ii.presentation.navigation.Route

private const val TASK_EDITOR_SCREEN_NAME = "task_editor"
private const val HISTORY_SCREEN_NAME = "history"

/**
 * Saver для сохранения и восстановления состояния маршрута навигации.
 *
 * Преобразует объект [Route] в строковый идентификатор, пригодный для сохранения через rememberSaveable,
 * и выполняет обратное восстановление маршрута.
 */
internal val RouteStateSaver = Saver<Route, String>(
    save = { route ->
        when (route) {
            Route.TaskEditor -> TASK_EDITOR_SCREEN_NAME
            Route.History -> HISTORY_SCREEN_NAME
        }
    },
    restore = { screenName ->
        when (screenName) {
            TASK_EDITOR_SCREEN_NAME -> Route.TaskEditor
            HISTORY_SCREEN_NAME -> Route.History
            else -> Route.TaskEditor
        }
    }
)
