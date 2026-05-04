package io.ii.data.di

import android.content.Context
import androidx.room.Room
import io.ii.data.local.TaskDatabase
import io.ii.data.local.dao.TaskDao
import io.ii.data.repository.TaskRepositoryImpl
import io.ii.domain.repository.TaskRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { provideDatabase(androidContext()) }
    single { provideDao(get()) }
    single<TaskRepository> { provideRepository(get(), get()) }
}

private fun provideDatabase(context: Context): TaskDatabase =
    Room.databaseBuilder(
        context = context,
        klass = TaskDatabase::class.java,
        name = TaskDatabase.DATABASE_NAME
    ).build()

private fun provideDao(db: TaskDatabase): TaskDao = db.taskDao()

private fun provideRepository(
    dao: TaskDao,
    db: TaskDatabase
): TaskRepository = TaskRepositoryImpl(
    dao = dao,
    db = db
)
