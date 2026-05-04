package io.ii.data.di

import android.content.Context
import androidx.room.Room
import io.ii.data.local.TaskDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { provideDatabase(androidContext()) }
}

private fun provideDatabase(context: Context): TaskDatabase =
    Room.databaseBuilder(
        context = context,
        klass = TaskDatabase::class.java,
        name = TaskDatabase.DATABASE_NAME
    ).build()
