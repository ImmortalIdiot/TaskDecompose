package io.ii.data.utils

/**
 * Хранит общие константы, которые используются в модуле `data`.
 */
internal object Constants {

    const val REQUEST_TIMEOUT_MILLIS: Long = 30_000
    const val CONNECTION_TIMEOUT_MILLIS: Long = 15_000
    const val SOCKET_TIMEOUT_MILLIS: Long = 30_000

    const val REQUEST_ID_HEADER = "RqUID"
    const val AUTH_BODY_SCOPE_KEY = "scope"
    const val AUTH_BODY_SCOPE_VALUE = "GIGACHAT_API_PERS"

    /**
     * Запас времени для того, чтобы токен не истек во время запроса, в мс.
     */
    const val TOKEN_EXPIRATION_SAFETY_TIMEOUT_MILLIS: Long = 60_000

    /**
     * Название модели, которая используется для генерации текста
     */
    const val GIGACHAT_MODEL = "GigaChat"
    const val USER_ROLE = "user"
}
