package io.ii.data.repository

import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task

/**
* Строит промпт для декомпозиции задачи.
*
* Формирует строку с описанием задачи и параметрами декомпозиции,
* которую можно передать языковой модели для получения структуры подзадач.
*/
internal object PromptBuilder {

    fun build(
        task: Task,
        params: DecompositionParams
    ): String {
        return buildString {
            appendLine("Ты декомпозируешь пользовательскую задачу на подзадачи.")
            appendLine("Верни ответ строго в JSON без Markdown и дополнительных пояснений.")
            appendLine()
            appendLine("Формат ответа — JSON-массив:")
            appendLine(
                """
                [
                  {
                    "title": "string",
                    "subtasks": []
                  }
                ]
                """.trimIndent()
            )

            appendLine()
            appendLine("Исходная задача:")
            appendLine("title: ${task.title}")
            appendLine("description: ${task.description ?: "null"}")

            appendLine()
            appendLine("Параметры декомпозиции:")
            appendLine("depth: ${params.depth}")

            appendLine()
            appendLine("Требования:")
            appendLine("- Верни только JSON-массив.")
            appendLine("- Не добавляй корневую задачу в ответ.")
            appendLine("- В ответе должны быть только подзадачи исходной задачи.")
            appendLine("- Каждый элемент массива должен содержать только поля title и subtasks.")
            appendLine("- Каждый элемент любого массива subtasks должен быть объектом такого же формата, а не строкой.")
            appendLine("- Поле title должно быть непустой строкой.")
            appendLine("- Поле subtasks должно быть массивом. Если вложенных подзадач нет, верни пустой массив.")
            appendLine("- Не используй строки, null или другие типы данных вместо объектов подзадач.")
            appendLine("- JSON должен быть валидным: двойные кавычки, без trailing comma, без незакрытых скобок.")
            appendLine("- Глубина вложенности subtasks не должна превышать ${params.depth}.")
            appendLine("- Каждая подзадача должна быть конкретным действием.")
            appendLine("- Сохрани смысл исходной задачи.")
            appendLine("- Не добавляй Markdown.")
            appendLine("- Не добавляй пояснения вне JSON.")

            if (params.hasPriority) {
                appendLine("- Учитывай приоритетность при порядке подзадач: более важные подзадачи должны идти раньше.")
            }

            if (params.hasTimeEstimation) {
                appendLine("- Учитывай примерную длительность выполнения при детализации подзадач, но не добавляй отдельное поле времени.")
            }
        }
    }
}
