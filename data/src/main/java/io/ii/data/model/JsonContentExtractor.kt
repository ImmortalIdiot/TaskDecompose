package io.ii.data.model

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray

/**
 * Извлекает строковые значения из JSON по точечному пути.
 *
 * Поддерживает имена полей объектов и числовые индексы массивов.
 */
internal object JsonContentExtractor {

    private const val PATH_SEPARATOR = '.'

    /**
     * Возвращает строку из JSON по указанному пути.
     *
     * @param root корневой JSON-элемент
     * @param path путь до строкового значения
     * @return найденное строковое значение
     */
    fun extractString(root: JsonElement, path: String): String {
        val result = path
            .split(PATH_SEPARATOR)
            .filter(String::isNotBlank)
            .fold(root) { current, segment ->
                if (segment.all(Char::isDigit)) {
                    current.jsonArray[segment.toInt()]
                } else {
                    (current as JsonObject)[segment] ?: error("Response field \"$segment\" not found")
                }
            }

        return (result as? JsonPrimitive)?.content ?: error("Response path \"$path\" does not point to a string")
    }
}
