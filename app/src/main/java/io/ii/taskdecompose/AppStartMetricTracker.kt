package io.ii.taskdecompose

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.SystemClock
import io.ii.data.metrics.AppStartType
import io.ii.data.metrics.MetricsReporter

/**
 * Отслеживает холодный и горячий старт приложения через жизненный цикл активности.
 *
 * Холодный старт измеряется от создания [Application] до первого [Activity.onActivityResumed].
 * Горячий старт измеряется от возвращения приложения из фонового состояния до следующего resume.
 *
 * @property processStartedAtMillis время создания процесса приложения в миллисекундах [SystemClock.elapsedRealtime]
 * @property metricsReporter отправитель собранных метрик
 */
class AppStartMetricTracker(
    private val processStartedAtMillis: Long,
    private val metricsReporter: MetricsReporter
) : Application.ActivityLifecycleCallbacks {

    private var startedActivitiesCount = 0
    private var coldStartReported = false
    private var hotStartStartedAtMillis: Long? = null

    override fun onActivityStarted(activity: Activity) {
        if (startedActivitiesCount == 0 && coldStartReported) {
            hotStartStartedAtMillis = SystemClock.elapsedRealtime()
        }

        startedActivitiesCount++
    }

    override fun onActivityResumed(activity: Activity) {
        if (!coldStartReported) {
            coldStartReported = true
            metricsReporter.reportAppStart(
                type = AppStartType.COLD,
                durationMillis = SystemClock.elapsedRealtime() - processStartedAtMillis
            )
            return
        }

        val hotStartStartedAt = hotStartStartedAtMillis ?: return
        hotStartStartedAtMillis = null
        metricsReporter.reportAppStart(
            type = AppStartType.HOT,
            durationMillis = SystemClock.elapsedRealtime() - hotStartStartedAt
        )
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivitiesCount = (startedActivitiesCount - 1).coerceAtLeast(0)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}
