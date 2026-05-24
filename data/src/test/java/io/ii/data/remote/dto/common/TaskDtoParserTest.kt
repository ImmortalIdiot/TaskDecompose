package io.ii.data.remote.dto.common

import kotlinx.serialization.SerializationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

/**
 * Проверяет устойчивый парсинг JSON-ответа декомпозиции.
 */
class TaskDtoParserTest {

    /**
     * Проверяет, что лишние поля модели не ломают разбор ответа.
     */
    @Test
    fun `parse ignores unknown fields`() {
        val tasks = TaskDtoParser.parse(
            """
            [
              {
                "title": "Подготовить данные",
                "subtasks": [
                  {
                    "title": "Собрать требования",
                    "subtests": []
                  }
                ]
              }
            ]
            """.trimIndent()
        )

        assertEquals("Подготовить данные", tasks.single().title)
        assertEquals("Собрать требования", tasks.single().subtasks.single().title)
    }

    /**
     * Проверяет, что JSON-подзадачи из ответа сервера сохраняют вложенную структуру.
     */
    @Test
    fun `parse converts json subtasks to nested task tree`() {
        val tasks = TaskDtoParser.parse(
            """
            [
              {
                "title": "Запустить проект",
                "subtasks": [
                  {
                    "title": "Подготовить репозиторий",
                    "subtasks": [
                      {
                        "title": "Настроить CI",
                        "subtasks": []
                      }
                    ]
                  },
                  {
                    "title": "Собрать первый релиз",
                    "subtasks": []
                  }
                ]
              }
            ]
            """.trimIndent()
        )

        val rootTask = tasks.single()

        assertEquals("Запустить проект", rootTask.title)
        assertEquals(listOf("Подготовить репозиторий", "Собрать первый релиз"), rootTask.subtasks.map { it.title })
        assertEquals("Настроить CI", rootTask.subtasks.first().subtasks.single().title)
    }

    /**
     * Проверяет, что строковые элементы subtasks превращаются в задачи.
     */
    @Test
    fun `parse converts string subtasks to task titles`() {
        val tasks = TaskDtoParser.parse(
            """
            [
              {
                "title": "Организовать встречу",
                "subtasks": [
                  "Выбрать дату",
                  {
                    "title": "Забронировать место",
                    "subtasks": []
                  }
                ]
              }
            ]
            """.trimIndent()
        )

        assertEquals(listOf("Выбрать дату", "Забронировать место"), tasks.single().subtasks.map { it.title })
    }

    /**
     * Проверяет, что строковые подзадачи поддерживаются на разных уровнях вложенности.
     */
    @Test
    fun `parse converts nested string subtasks to task titles`() {
        val tasks = TaskDtoParser.parse(
            """
            [
              {
                "title": "Подготовить презентацию",
                "subtasks": [
                  "Собрать материалы",
                  {
                    "title": "Оформить слайды",
                    "subtasks": [
                      "Добавить диаграммы",
                      "Проверить текст"
                    ]
                  }
                ]
              }
            ]
            """.trimIndent()
        )

        val rootTask = tasks.single()

        assertEquals("Собрать материалы", rootTask.subtasks.first().title)
        assertEquals(listOf("Добавить диаграммы", "Проверить текст"), rootTask.subtasks.last().subtasks.map { it.title })
    }

    /**
     * Проверяет извлечение JSON-массива из текстовой обертки.
     */
    @Test
    fun `parse extracts json array from text wrapper`() {
        val tasks = TaskDtoParser.parse(
            """
            Вот JSON:
            [
              {
                "title": "Сделать задачу",
                "subtasks": []
              }
            ]
            """.trimIndent()
        )

        assertEquals("Сделать задачу", tasks.single().title)
    }

    /**
     * Проверяет, что полностью невалидный JSON остается ошибкой.
     */
    @Test
    fun `parse fails on invalid json`() {
        assertThrows(SerializationException::class.java) {
            TaskDtoParser.parse("""[{"title": "Broken", "subtasks": [""")
        }
    }
}
