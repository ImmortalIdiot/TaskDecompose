package io.ii.presentation.utils

import io.ii.presentation.states.SubtaskState

/**
 * Хранит общие константы, которые используются в модуле `presentation`.
 */
internal object Constants {

    const val SLIDE_ANIMATION_DURATION: Int = 350 // in millis
    const val FADE_ANIMATION_DURATION: Int = 250 // in millis

    const val SETTINGS_SNACKBAR_DURATION = 3_000L // in millis

    const val MIN_DECOMPOSITION_DEPTH = 1f
    const val MAX_DECOMPOSITION_DEPTH = 5f

    val MOCK_SUBTASKS = listOf(
        SubtaskState(
            id = "1",
            title = "Удалить ненужные программы",
            description = null,
            createdAt = 0L,
            subtasks = listOf(
                SubtaskState(
                    id = "1.1",
                    title = "Открыть список установленных программ",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "1.2",
                    title = "Найти редко используемые приложения",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "1.3",
                    title = "Удалить ненужные программы",
                    description = null,
                    createdAt = 0L
                )
            )
        ),
        SubtaskState(
            id = "2",
            title = "Очистить временные файлы",
            description = null,
            createdAt = 0L,
            subtasks = listOf(
                SubtaskState(
                    id = "2.1",
                    title = "Очистить корзину",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "2.2",
                    title = "Удалить временные файлы системы",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "2.3",
                    title = "Очистить папку Downloads",
                    description = null,
                    createdAt = 0L,
                    subtasks = listOf(
                        SubtaskState(
                            id = "2.3.1",
                            title = "Удалить старые архивы",
                            description = null,
                            createdAt = 0L
                        ),
                        SubtaskState(
                            id = "2.3.2",
                            title = "Удалить дубликаты файлов",
                            description = null,
                            createdAt = 0L
                        )
                    )
                )
            )
        ),
        SubtaskState(
            id = "3",
            title = "Проверить автозагрузку",
            description = null,
            createdAt = 0L,
            subtasks = listOf(
                SubtaskState(
                    id = "3.1",
                    title = "Открыть диспетчер задач",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "3.2",
                    title = "Отключить лишние программы из автозагрузки",
                    description = null,
                    createdAt = 0L
                )
            )
        ),
        SubtaskState(
            id = "4",
            title = "Проверить компьютер на вредоносное ПО",
            description = null,
            createdAt = 0L,
            subtasks = listOf(
                SubtaskState(
                    id = "4.1",
                    title = "Обновить антивирусные базы",
                    description = null,
                    createdAt = 0L
                ),
                SubtaskState(
                    id = "4.2",
                    title = "Запустить полное сканирование",
                    description = null,
                    createdAt = 0L
                )
            )
        )
    )
}
