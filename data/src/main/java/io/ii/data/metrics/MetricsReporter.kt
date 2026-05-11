package io.ii.data.metrics

/**
 * Собирает ключевые метрики работы приложения:
 * - Время холодного и горячего старта приложения
 * - Время декомпозиции задачи уровня API
 * - Количество потраченных токенов:
 *     - Количество токенов во входящем сообщении
 *     - Количество токенов, сгенерированных моделью
 *     - Общее число токенов, потраченных на запрос-ответ
 */
interface MetricsReporter {

    /**
     * Отправляет метрику старта приложения.
     *
     * @param type тип старта приложения
     * @param durationMillis длительность старта в миллисекундах
     */
    fun reportAppStart(
        type: AppStartType,
        durationMillis: Long
    )

    /**
     * Отправляет метрику декомпозиции задачи.
     *
     * @param requestResponseDurationMillis длительность запроса-ответа API в миллисекундах
     * @param promptTokens количество токенов пользовательского запроса
     * @param completionTokens количество токенов ответа модели
     * @param totalTokens общий расход токенов
     */
    fun reportTaskDecomposition(
        requestResponseDurationMillis: Long,
        promptTokens: Long?,
        completionTokens: Long?,
        totalTokens: Long?
    )
}

/**
 * Тип старта приложения для отправки в аналитику.
 *
 * @property analyticsValue значение параметра, которое передаётся в Firebase Analytics
 */
enum class AppStartType(
    val analyticsValue: String
) {
    /** Первый запуск UI после создания процесса приложения. */
    COLD("cold"),

    /** Возврат приложения из фонового состояния. */
    HOT("hot")
}
