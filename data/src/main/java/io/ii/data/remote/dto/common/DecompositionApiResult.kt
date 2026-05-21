package io.ii.data.remote.dto.common

/**
 * Результат запроса декомпозиции к API модели.
 *
 * Объединяет распарсенные подзадачи, статистику расхода токенов и длительность сетевого запроса.
 *
 * @property tasks список подзадач, сгенерированных моделью
 * @property usage статистика расхода токенов, если API вернул поле usage
 * @property requestResponseDurationMillis длительность запроса-ответа API в миллисекундах
 */
internal data class DecompositionApiResult(
    val tasks: List<TaskDto>,
    val usage: TokenUsage?,
    val requestResponseDurationMillis: Long
)
