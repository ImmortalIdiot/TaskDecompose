package io.ii.taskdecompose

import android.app.Application
import io.ii.data.di.dataModule
import io.ii.presentation.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class TaskDecompose : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@TaskDecompose)
            modules(
                dataModule,
                presentationModule
            )
        }
    }
}