package io.ii.data.di

import android.content.Context
import androidx.room.Room
import io.ii.data.R
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
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

val dataModule = module {
    single { provideDatabase(androidContext()) }
    single { provideDao(get()) }

    single { provideOkHttpClient(androidContext()) }
    single { provideApiClient(get()) }
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

private fun provideApiClient(
    okHttpClient: OkHttpClient
): HttpClient {
    return HttpClient(OkHttp) {

        engine {
            preconfigured = okHttpClient
        }

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

private const val CERTIFICATE_FACTORY_INSTANCE = "X.509"
private const val SSL_PROTOCOL = "TLS"

private fun provideOkHttpClient(
    context: Context
) : OkHttpClient {

    val certificateFactory = CertificateFactory.getInstance(CERTIFICATE_FACTORY_INSTANCE)

    val certs = listOf(
        R.raw.russian_trusted_root_ca,
        R.raw.russian_trusted_sub_ca
    ).map { id ->
        context.resources.openRawResource(id).use { stream ->
            certificateFactory.generateCertificate(stream)
        }
    }

    val keyStore = KeyStore.getInstance(
        KeyStore.getDefaultType()
    ).apply {
        load(null, null)

        certs.forEachIndexed { index, certificate ->
            setCertificateEntry("cert_$index", certificate)
        }
    }

    val trustManagerFactory = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm()
    ).apply {
        init(keyStore)
    }

    val trustManager = trustManagerFactory.trustManagers
        .filterIsInstance<X509TrustManager>()
        .first()

    val sslContext = SSLContext.getInstance(SSL_PROTOCOL).apply {
        init(null, arrayOf(trustManager), null)
    }

    return OkHttpClient
        .Builder()
        .sslSocketFactory(sslContext.socketFactory, trustManager)
        .build()
}
