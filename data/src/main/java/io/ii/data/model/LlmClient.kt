package io.ii.data.model

import io.ii.data.remote.dto.DecompositionApiResult

/**
 * Клиент языковой модели, способный выполнить декомпозицию по готовому промпту.
 */
internal interface LlmClient {

    /**
     * Отправляет промпт модели и возвращает распарсенный результат декомпозиции.
     *
     * @param prompt промпт с описанием задачи и требованиями к JSON-ответу
     * @return результат запроса к API модели
     */
    suspend fun decomposeTask(prompt: String): DecompositionApiResult
}
