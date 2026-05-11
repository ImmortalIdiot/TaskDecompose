package io.ii.data.metrics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import timber.log.Timber

/**
 * Реализация [MetricsReporter], отправляющая метрики в Firebase Analytics.
 *
 * Если Firebase не сконфигурирован или отправка события завершилась ошибкой,
 * репортёр пишет предупреждение в лог и не прерывает основной пользовательский сценарий.
 *
 * @param context Android context, используемый для получения экземпляра [FirebaseAnalytics]
 */
class FirebaseMetricsReporter(
    context: Context
) : MetricsReporter {

    private val analytics: FirebaseAnalytics? = runCatching {
        FirebaseAnalytics.getInstance(context.applicationContext)
    }.onFailure { error ->
        Timber.tag(TAG).w(error, "Firebase Analytics is not configured")
    }.getOrNull()

    override fun reportAppStart(
        type: AppStartType,
        durationMillis: Long
    ) {
        logEvent(
            name = EVENT_APP_START_METRIC,
            params = Bundle().apply {
                putString(PARAM_START_TYPE, type.analyticsValue)
                putLong(PARAM_STARTUP_DURATION_MILLIS, durationMillis)
            }
        )
    }

    override fun reportTaskDecomposition(
        requestResponseDurationMillis: Long,
        promptTokens: Long?,
        completionTokens: Long?,
        totalTokens: Long?
    ) {
        logEvent(
            name = EVENT_TASK_DECOMPOSITION_METRIC,
            params = Bundle().apply {
                putLong(PARAM_DECOMPOSITION_DURATION_MILLIS, requestResponseDurationMillis)
                promptTokens?.let { putLong(PARAM_PROMPT_TOKENS, it) }
                completionTokens?.let { putLong(PARAM_COMPLETION_TOKENS, it) }
                totalTokens?.let { putLong(PARAM_TOTAL_TOKENS, it) }
            }
        )
    }

    private fun logEvent(name: String, params: Bundle) {
        runCatching {
            analytics?.logEvent(name, params)
        }.onFailure { error ->
            Timber.tag(TAG).w(error, "Failed to report Firebase metric: %s", name)
        }
    }

    private companion object {
        const val TAG = "FirebaseMetrics"

        const val EVENT_APP_START_METRIC = "app_start_metric"
        const val EVENT_TASK_DECOMPOSITION_METRIC = "task_decomposition_metric"

        const val PARAM_START_TYPE = "start_type"
        const val PARAM_STARTUP_DURATION_MILLIS = "startup_duration_ms"
        const val PARAM_DECOMPOSITION_DURATION_MILLIS = "decomposition_duration_ms"
        const val PARAM_PROMPT_TOKENS = "prompt_tokens"
        const val PARAM_COMPLETION_TOKENS = "completion_tokens"
        const val PARAM_TOTAL_TOKENS = "total_tokens"
    }
}
