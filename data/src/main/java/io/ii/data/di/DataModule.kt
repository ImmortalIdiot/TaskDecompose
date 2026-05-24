package io.ii.data.di

import android.content.Context
import androidx.room.Room
import io.ii.data.R
import io.ii.data.local.model.LlmSettingsStorage
import io.ii.data.local.task.TaskDatabase
import io.ii.data.local.task.dao.TaskDao
import io.ii.data.local.token.AccessTokenStorage
import io.ii.data.metrics.FirebaseMetricsReporter
import io.ii.data.metrics.MetricsReporter
import io.ii.data.model.LlmRouter
import io.ii.data.remote.api.gigachat.GigaChatApi
import io.ii.data.repository.LlmSettingsRepositoryImpl
import io.ii.data.repository.TaskRepositoryImpl
import io.ii.data.utils.Constants
import io.ii.domain.repository.LlmSettingsRepository
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
import java.security.KeyStore
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

val dataModule = module {
    single { provideDatabase(androidContext()) }
    single { provideDao(get()) }

    single { provideOkHttpClient(androidContext()) }
    single { provideApiClient(get()) }
    single { AccessTokenStorage(androidContext()) }
    single { GigaChatApi(get(), get()) }
    single { LlmSettingsStorage(androidContext()) }
    single<LlmSettingsRepository> { LlmSettingsRepositoryImpl(get()) }
    single { LlmRouter(get(), get(), get()) }
    single<MetricsReporter> { FirebaseMetricsReporter(androidContext()) }

    single<TaskRepository> {
        provideRepository(get(), get(), get(), get())
    }
}

/**
 * Создает Room БД для хранения дерева декомпозированных задач.
 *
 * @param context Android context, используемый для инициализации Room
 *
 * @return экземпляр [TaskDatabase]
 */
private fun provideDatabase(context: Context): TaskDatabase =
    Room.databaseBuilder(
        context = context,
        klass = TaskDatabase::class.java,
        name = TaskDatabase.DATABASE_NAME
    )
        .addMigrations(TaskDatabase.MIGRATION_1_2)
        .build()

private fun provideDao(db: TaskDatabase): TaskDao = db.taskDao()

/**
 * Создает реализацию репозитория задач.
 *
 * @param dao DAO для доступа к локальным задачам
 * @param db БД с задачами для создания собственных транзакций
 * @param llmRouter роутер выбранного API модели
 * @param metricsReporter отправитель метрик приложения
 *
 * @return реализация [TaskRepository]
 */
private fun provideRepository(
    dao: TaskDao,
    db: TaskDatabase,
    llmRouter: LlmRouter,
    metricsReporter: MetricsReporter
): TaskRepository = TaskRepositoryImpl(
    dao = dao,
    db = db,
    llmRouter = llmRouter,
    metricsReporter = metricsReporter
)

/**
 * Создает Ktor HTTP client поверх настроенного [OkHttpClient].
 *
 * Подключает JSON сериализацию, таймауты и базовые заголовки для запросов к API.
 *
 * @param okHttpClient OkHttp client с настроенным SSL контекстом
 *
 * @return экземпляр [HttpClient]
 */
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
                    encodeDefaults = false
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

/**
 * Создает OkHttp client с системными доверенными сертификатами и сертификатами Минцифры.
 *
 * @param context Android context для чтения raw ресурсов сертификатов
 *
 * @return экземпляр [OkHttpClient]
 */
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

    val defaultTrustManager = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm()
    ).apply {
        init(null as KeyStore?)
    }.x509TrustManager()

    val customTrustManager = TrustManagerFactory.getInstance(
        TrustManagerFactory.getDefaultAlgorithm()
    ).apply {
        init(keyStore)
    }.x509TrustManager()

    val trustManager = CompositeX509TrustManager(
        trustManagers = listOf(defaultTrustManager, customTrustManager)
    )

    val sslContext = SSLContext.getInstance(SSL_PROTOCOL).apply {
        init(null, arrayOf(trustManager), null)
    }

    return OkHttpClient
        .Builder()
        .sslSocketFactory(sslContext.socketFactory, trustManager)
        .build()
}

private fun TrustManagerFactory.x509TrustManager(): X509TrustManager {
    return trustManagers
        .filterIsInstance<X509TrustManager>()
        .first()
}

private class CompositeX509TrustManager(
    private val trustManagers: List<X509TrustManager>
) : X509TrustManager {

    override fun checkClientTrusted(
        chain: Array<out X509Certificate>?,
        authType: String?
    ) {
        checkTrusted { trustManager ->
            trustManager.checkClientTrusted(chain, authType)
        }
    }

    override fun checkServerTrusted(
        chain: Array<out X509Certificate>?,
        authType: String?
    ) {
        checkTrusted { trustManager ->
            trustManager.checkServerTrusted(chain, authType)
        }
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return trustManagers
            .flatMap { trustManager -> trustManager.acceptedIssuers.toList() }
            .toTypedArray()
    }

    private fun checkTrusted(block: (X509TrustManager) -> Unit) {
        var lastError: CertificateException? = null

        trustManagers.forEach { trustManager ->
            try {
                block(trustManager)
                return
            } catch (error: CertificateException) {
                lastError = error
            }
        }

        throw lastError ?: CertificateException("No trust manager accepted certificate chain")
    }
}
