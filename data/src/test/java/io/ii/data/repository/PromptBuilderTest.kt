package io.ii.data.repository

import io.ii.domain.model.DecompositionParams
import io.ii.domain.model.Task
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Проверяет формирование промпта для декомпозиции задачи.
 *
 * Эти тесты фиксируют обязательные части запроса к модели и условия, которые зависят от параметров декомпозиции.
 */
class PromptBuilderTest {

    /**
     * Проверяет, что промпт содержит исходные данные задачи и ограничение глубины.
     */
    @Test
    fun `build includes task fields and depth requirement`() {
        val prompt = PromptBuilder.build(
            task = Task(
                id = "id",
                title = "Запустить проект",
                description = "Нужен MVP",
                createdAt = 1L
            ),
            params = DecompositionParams(
                depth = 3,
                hasPriority = false
            )
        )

        assertTrue(prompt.contains("title: Запустить проект"))
        assertTrue(prompt.contains("description: Нужен MVP"))
        assertTrue(prompt.contains("depth: 3"))
        assertTrue(prompt.contains("Глубина вложенности subtasks не должна превышать 3"))
        assertFalse(prompt.contains("Учитывай приоритетность"))
    }

    /**
     * Проверяет обработку отсутствующего описания и добавление опциональных требований.
     */
    @Test
    fun `build writes null description and optional requirements`() {
        val prompt = PromptBuilder.build(
            task = Task(
                id = "id",
                title = "Запустить проект",
                description = null,
                createdAt = 1L
            ),
            params = DecompositionParams(
                depth = 2,
                hasPriority = true
            )
        )

        assertTrue(prompt.contains("description: null"))
        assertTrue(prompt.contains("Учитывай приоритетность"))
    }
}
