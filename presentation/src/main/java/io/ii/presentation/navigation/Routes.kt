package io.ii.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Маршрут bottom-навигации приложения.
 *
 * @property index индекс маршрута в панели навигации
 */
@Serializable
internal sealed class Route(val index: Int) {

    /**
     * Экран редактирования и декомпозиции задачи.
     *
     * @property taskId идентификатор задачи для открытия из истории
     */
    @Serializable
    data class TaskEditor(val taskId: String? = null) : Route(TASK_EDITOR_INDEX)

    /**
     * Экран истории созданных декомпозиций.
     */
    @Serializable
    data object History : Route(HISTORY_INDEX)

    /**
     * Экран выбора модели и параметров доступа к API.
     */
    @Serializable
    data object ModelSettings : Route(MODEL_SETTINGS_INDEX)

    companion object {
        /**
         * Все маршруты, отображаемые в нижней навигации.
         */
        val ALL = listOf(TaskEditor(), History, ModelSettings)

        private const val TASK_EDITOR_INDEX = 0
        private const val HISTORY_INDEX = 1
        private const val MODEL_SETTINGS_INDEX = 2
    }
}
