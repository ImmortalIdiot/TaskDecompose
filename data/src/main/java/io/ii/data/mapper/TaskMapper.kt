package io.ii.data.mapper

import io.ii.data.local.entity.TaskEntity
import io.ii.domain.model.Task

/**
 * Преобразует доменную модель задачи в сущность базы данных.
 *
 * Используется для сохранения задачи в Room. Связь с родительской задачей
 * задаётся через параметр [parentId].
 *
 * @param parentId идентификатор родительской задачи. Для корневой задачи равен null
 * @return сущность задачи для хранения в базе данных
 */
fun Task.toEntity(parentId: String? = null): TaskEntity =
    TaskEntity(
        id = id,
        parentId = parentId,
        title = title,
        description = description,
        createdAt = createdAt
    )

/**
 * Преобразует задачу и все её подзадачи в плоский список сущностей.
 *
 * Выполняет рекурсивный обход дерева задач и формирует список [TaskEntity], пригодный для сохранения в БД.
 *
 * @param parentId идентификатор родительской задачи. Для корневой задачи - null
 * @return список сущностей, включающий текущую задачу и все вложенные подзадачи
 */
fun Task.toEntities(parentId: String? = null): List<TaskEntity> = listOf(
    toEntity(parentId)
) + subtasks.flatMap {
    it.toEntities(parentId = id)
}

/**
 * Преобразует плоский список сущностей задач в список корневых доменных моделей.
 *
 * Восстанавливает древовидную структуру задач на основе идентификатора родительской задачи.
 *
 * @return список корневых задач с вложенными подзадачами
 */
fun List<TaskEntity>.toModelTree(): List<Task> {
    val childrenByParent = groupBy { it.parentId }

    return childrenByParent[null]
        ?.map { entity ->
            entity.toModel(childrenByParent)
        }
        .orEmpty()
}

/**
 * Преобразует список сущностей в доменную модель задачи с указанным идентификатором.
 *
 * Восстанавливает поддерево только для заданной задачи, без построения полного дерева.
 *
 * @param id идентификатор задачи
 * @return задача с вложенными подзадачами или null, если задача не найдена
 */
fun List<TaskEntity>.toModel(id: String): Task? {
    val entityById = associateBy { it.id }
    val childrenByParent = groupBy { it.parentId }

    return entityById[id]?.toModel(childrenByParent)
}

/**
 * Преобразует сущность задачи в доменную модель с учётом вложенных подзадач.
 *
 * Выполняет рекурсивную сборку дерева задач на основе отображения
 * "родитель -> список дочерних задач".
 *
 * Использует [visitedIds] для обнаружения циклов в структуре и предотвращения
 * бесконечной рекурсии.
 *
 * @param childrenByParent отображение parentId -> список дочерних сущностей
 * @param visitedIds множество уже посещённых идентификаторов для защиты от циклов
 * @return доменная модель задачи с вложенными подзадачами
 * @throws IllegalStateException если обнаружен цикл в структуре задач
 */
private fun TaskEntity.toModel(
    childrenByParent: Map<String?, List<TaskEntity>>,
    visitedIds: Set<String> = emptySet()
): Task {
    if (id in visitedIds) {
        error("Cycle in task tree. Task id: $id")
    }

    val newVisitedIds = visitedIds + id

    return Task(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        subtasks = childrenByParent[id]
            ?.map { child ->
                child.toModel(
                    childrenByParent = childrenByParent,
                    visitedIds = newVisitedIds
                )
            }
            .orEmpty()
    )
}

// TODO: add mapping domain to request (by prompt builder)
// TODO: add mapping response to domain
