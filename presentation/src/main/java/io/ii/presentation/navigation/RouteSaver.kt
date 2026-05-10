package io.ii.presentation.navigation

import androidx.compose.runtime.saveable.Saver

private const val TASK_EDITOR_SCREEN_NAME = "task_editor"
private const val HISTORY_SCREEN_NAME = "history"

/**
 * Saver для сохранения и восстановления состояния маршрута навигации.
 *
 * Преобразует объект [Route] в строковый идентификатор, пригодный для сохранения через rememberSaveable,
 * и выполняет обратное восстановление маршрута.
 */
internal val RouteBackStackSaver = Saver<List<Route>, List<String>>(
    save = { routes ->
        routes.map { route ->
            when (route) {
                Route.TaskEditor -> TASK_EDITOR_SCREEN_NAME
                Route.History -> HISTORY_SCREEN_NAME
            }
        }
    },
    restore = { screenNames ->
        screenNames.map { screenName ->
            when (screenName) {
                TASK_EDITOR_SCREEN_NAME -> Route.TaskEditor
                HISTORY_SCREEN_NAME -> Route.History
                else -> Route.TaskEditor
            }
        }.ifEmpty {
            listOf(Route.TaskEditor)
        }
    }
)
