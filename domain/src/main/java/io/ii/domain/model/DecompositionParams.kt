package io.ii.domain.model

/**
 * Параметры декомпозиции задачи.
 *
 * Определяют поведение алгоритма разбиения задачи на подзадачи.
 * Используются при формировании запроса к модели для генерации результата.
 *
 * @property depth глубина декомпозиции (уровень вложенности)
 * @property hasPriority учитывать ли приоритизацию подзадач
 * @property hasTimeEstimation добавлять ли оценку времени для подзадач
 */
data class DecompositionParams(
    val depth: Int,
    val hasPriority: Boolean,
    val hasTimeEstimation: Boolean
)
