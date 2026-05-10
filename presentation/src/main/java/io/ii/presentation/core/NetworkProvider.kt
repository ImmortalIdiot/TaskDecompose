package io.ii.presentation.core

/**
 * Провайдер состояния интернет-соединения.
 */
internal interface NetworkProvider {

    /**
     * Проверяет наличие интернет-соединения.
     */
    fun hasInternetConnection(): Boolean
}
