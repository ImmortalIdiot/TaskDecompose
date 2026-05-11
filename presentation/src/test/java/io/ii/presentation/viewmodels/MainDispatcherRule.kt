package io.ii.presentation.viewmodels

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
/**
 * JUnit-правило для подмены Main-диспетчера в тестах ViewModel.
 *
 * Позволяет запускать корутины из viewModelScope в JVM unit-тестах без Android main looper.
 */
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    /**
     * Устанавливает тестовый диспетчер перед запуском каждого теста.
     */
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    /**
     * Возвращает Main-диспетчер в исходное состояние после завершения теста.
     */
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
