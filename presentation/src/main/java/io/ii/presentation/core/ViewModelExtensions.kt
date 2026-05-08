package io.ii.presentation.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ii.presentation.utils.LoggingTags
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Безопасно запускает корутину в [viewModelScope].
 *
 * Предоставляет обработку:
 * - начала выполнения;
 * - ошибок;
 * - завершения выполнения.
 *
 * При отмене корутины повторно выбрасывает [CancellationException].
 *
 * @param onError обработчик ошибок
 * @param start вызывается перед запуском body
 * @param final вызывается после завершения body
 * @param body основной suspend блок
 *
 * @return Job запущенной корутины
 */
fun ViewModel.launchSafe(
    onError: ((error: Throwable) -> Unit)? = null,
    start: (() -> Unit)? = null,
    final: (() -> Unit)? = null,
    body: suspend () -> Unit
): Job = viewModelScope.launch {
    try {
        start?.invoke()
        body()
    } catch (error: Exception) {

        if (!isActive && error is CancellationException) {
            Timber.tag(LoggingTags.LAUNCH_SAFE).w("${error.message}")
            throw error
        }

        Timber.tag(LoggingTags.LAUNCH_SAFE).e(error)
        onError?.invoke(error)
    } finally {
        final?.invoke()
    }
}
