package io.ii.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Сущность в базе данных, представляющая доменную модель [io.ii.domain.model.Task].
 *
 * Список подзадач хранится в виде отдельных сущностей с указателем на родительский идентификатор в виде поля [parentId].
 *
 * @param id идентификатор задачи;
 * @param parentId идентификатор родительской задачи;
 * @param title текст задачи;
 * @param description возможное описание задачи;
 * @param createdAt дата создания задачи.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
internal data class TaskEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "parent_id") val parentId: String?,
    val title: String,
    val description: String?,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
