package io.ii.presentation.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

@OptIn(ExperimentalCoroutinesApi::class)
/**
 * JUnit-расширение для подмены Main-диспетчера в тестах ViewModel.
 *
 * Позволяет запускать корутины из viewModelScope в JVM unit-тестах без Android main looper.
 */
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : BeforeEachCallback, AfterEachCallback {

    /**
     * Устанавливает тестовый диспетчер перед запуском каждого теста.
     */
    override fun beforeEach(context: ExtensionContext) {
        Dispatchers.setMain(dispatcher)
    }

    /**
     * Возвращает Main-диспетчер в исходное состояние после завершения теста.
     */
    override fun afterEach(context: ExtensionContext) {
        Dispatchers.resetMain()
    }
}
