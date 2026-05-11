package io.ii.taskdecompose

import android.app.Application
import android.os.SystemClock
import io.ii.data.di.dataModule
import io.ii.data.metrics.FirebaseMetricsReporter
import io.ii.domain.di.domainModule
import io.ii.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class TaskDecompose : Application() {

    private val processStartedAtMillis = SystemClock.elapsedRealtime()

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@TaskDecompose)
            modules(
                dataModule,
                domainModule,
                presentationModule
            )
        }

        registerActivityLifecycleCallbacks(
            AppStartMetricTracker(
                processStartedAtMillis = processStartedAtMillis,
                metricsReporter = FirebaseMetricsReporter(this)
            )
        )
    }
}
