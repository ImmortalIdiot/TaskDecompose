package io.ii.data.di

import android.content.Context
import androidx.room.Room
import io.ii.data.local.task.TaskDatabase
import io.ii.data.local.task.dao.TaskDao
import io.ii.data.local.token.AccessTokenStorage
import io.ii.data.remote.api.GigaChatApi
import io.ii.data.repository.TaskRepositoryImpl
import io.ii.data.utils.Constants
import io.ii.domain.repository.TaskRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.accept
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { provideDatabase(androidContext()) }
    single { provideDao(get()) }

    single { provideApiClient() }
    single { GigaChatApi(get()) }
    single { AccessTokenStorage(androidContext()) }

    single<TaskRepository> { provideRepository(get(), get(), get(), get()) }
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
    db: TaskDatabase,
    api: GigaChatApi,
    tokenStorage: AccessTokenStorage
): TaskRepository = TaskRepositoryImpl(
    dao = dao,
    db = db,
    api = api,
    tokenStorage = tokenStorage
)

private fun provideApiClient(): HttpClient {
    return HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    isLenient = true
                }
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = Constants.REQUEST_TIMEOUT_MILLIS
            connectTimeoutMillis = Constants.CONNECTION_TIMEOUT_MILLIS
            socketTimeoutMillis = Constants.SOCKET_TIMEOUT_MILLIS
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }
    }
}
