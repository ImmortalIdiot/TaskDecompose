package io.ii.data.remote.dto.common

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.annotations.ApiStatus.Experimental

/**
 * Парсер JSON-ответа декомпозиции.
 *
 * Обрабатывает типичные неточности LLM-ответов: лишние поля, текстовые обертки
 * вокруг JSON-массива и строковые элементы внутри subtasks.
 */
@Experimental
internal object TaskDtoParser {

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        isLenient = true
    }

    /**
     * Преобразует текстовый ответ модели в список DTO задач.
     *
     * @param content текст, извлеченный из ответа модели
     * @return список задач из ответа модели
     */
    fun parse(content: String): List<TaskDto> {
        val jsonContent = content.extractJsonArray()

        return runCatching {
            json.decodeFromString<List<TaskDto>>(jsonContent)
        }.getOrElse {
            json.parseToJsonElement(jsonContent)
                .jsonArray
                .mapNotNull(::parseTask)
        }
    }

    private fun parseTask(element: JsonElement): TaskDto? {
        return when (element) {
            is JsonObject -> element.toTaskDto()
            is JsonPrimitive -> element.contentOrNull
                ?.takeIf(String::isNotBlank)
                ?.let { title -> TaskDto(title = title) }
            else -> null
        }
    }

    private fun JsonObject.toTaskDto(): TaskDto? {
        val title = get(TITLE_KEY)
            ?.jsonPrimitive
            ?.contentOrNull
            ?.takeIf(String::isNotBlank)
            ?: return null

        val subtasks = get(SUBTASKS_KEY)
            ?.let(::parseSubtasks)
            .orEmpty()

        return TaskDto(
            title = title,
            subtasks = subtasks
        )
    }

    private fun parseSubtasks(element: JsonElement): List<TaskDto> {
        return when (element) {
            is JsonArray -> element.mapNotNull(::parseTask)
            is JsonObject,
            is JsonPrimitive -> listOfNotNull(parseTask(element))
            else -> emptyList()
        }
    }

    private fun String.extractJsonArray(): String {
        val trimmed = trim()
        if (trimmed.startsWith("[")) {
            return trimmed
        }

        val startIndex = trimmed.indexOf('[')
        val endIndex = trimmed.lastIndexOf(']')

        if (startIndex !in 0..<endIndex) {
            throw SerializationException("Task decomposition response does not contain JSON array")
        }

        return trimmed.substring(startIndex, endIndex + 1)
    }

    private const val TITLE_KEY = "title"
    private const val SUBTASKS_KEY = "subtasks"
}
