package io.ii.domain.model

/**
 * Элемент истории декомпозиции.
 *
 * Хранит доменную задачу и метаданные, относящиеся к записи в истории,
 * но не к самому дереву задачи.
 *
 * @property task сохранённая задача с подзадачами
 * @property llmModelName название модели, использованной для декомпозиции
 */
data class TaskHistoryItem(
    val task: Task,
    val llmModelName: String?
)
