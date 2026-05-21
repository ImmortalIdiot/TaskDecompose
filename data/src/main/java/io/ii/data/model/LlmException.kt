package io.ii.data.model

/**
 * Ошибка временного ограничения API модели.
 *
 * Используется для rate limit ответов, которые не должны использовать retry.
 *
 * @param message текст ошибки, полученный от API
 */
internal class LlmRateLimitException(
    message: String
) : IllegalStateException(message)
