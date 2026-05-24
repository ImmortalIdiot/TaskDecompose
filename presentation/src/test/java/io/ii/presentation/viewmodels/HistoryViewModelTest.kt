package io.ii.presentation.viewmodels

import io.ii.domain.model.Task
import io.ii.domain.usecase.DeleteTaskUseCase
import io.ii.domain.usecase.LoadDecompositionHistoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
/**
 * Проверяет бизнес-логику экрана истории на уровне ViewModel.
 *
 * Эти тесты фиксируют сортировку задач и группировку по дате создания.
 */
class HistoryViewModelTest {

    @RegisterExtension
    @JvmField
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = Clock.fixed(
        Instant.parse("2026-01-02T12:00:00Z"),
        ZoneOffset.UTC
    )

    /**
     * Проверяет, что история сортируется от новых задач к старым
     * и группируется по календарной дате создания.
     */
    @Test
    fun `loadHistory sorts tasks by creation time and groups by date`() {
        val repository = FakeTaskRepository()
        val viewModel = viewModel(repository)

        repository.emitHistory(
            listOf(
                Task("older", "Older", null, timestamp("2026-01-01T12:00:00Z")),
                Task("newer", "Newer", null, timestamp("2026-01-02T09:00:00Z")),
                Task("sameDay", "Same day", null, timestamp("2026-01-02T08:00:00Z"))
            )
        )

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(listOf("Сегодня", "Вчера"), state.groups.map { it.date })
        assertEquals(listOf("newer", "sameDay"), state.groups.first().tasks.map { it.task.id })
    }

    /**
     * Проверяет форматирование дат, которые не относятся к сегодняшнему или вчерашнему дню.
     */
    @Test
    fun `loadHistory formats older dates with russian month name`() {
        val repository = FakeTaskRepository()
        val viewModel = viewModel(repository)

        repository.emitHistory(
            listOf(
                Task("older", "Older", null, timestamp("2026-12-15T12:00:00Z"))
            )
        )

        assertEquals("15 декабря 2026", viewModel.uiState.value.groups.single().date)
    }

    /**
     * Проверяет, что очистка без выбора удаляет всю историю.
     */
    @Test
    fun `deleteSelectedOrAllHistory deletes all tasks when nothing is selected`() = runTest {
        val repository = FakeTaskRepository()
        val viewModel = viewModel(repository)

        repository.emitHistory(
            listOf(
                Task("first", "First", null, timestamp("2026-01-02T09:00:00Z")),
                Task("second", "Second", null, timestamp("2026-01-01T08:00:00Z"))
            )
        )
        viewModel.deleteSelectedOrAllHistory()
        waitUntil { repository.deletedTaskIds.size == 2 }

        assertEquals(setOf("first", "second"), repository.deletedTaskIds.toSet())
    }

    /**
     * Проверяет, что при выборе карточек удаляются только выбранные задачи.
     */
    @Test
    fun `deleteSelectedOrAllHistory deletes selected tasks only`() = runTest {
        val repository = FakeTaskRepository()
        val viewModel = viewModel(repository)

        repository.emitHistory(
            listOf(
                Task("first", "First", null, timestamp("2026-01-02T09:00:00Z")),
                Task("second", "Second", null, timestamp("2026-01-01T08:00:00Z"))
            )
        )
        viewModel.toggleTaskSelection("second")
        viewModel.deleteSelectedOrAllHistory()
        waitUntil { repository.deletedTaskIds.isNotEmpty() }

        assertEquals(listOf("second"), repository.deletedTaskIds)
        assertEquals(emptySet<String>(), viewModel.uiState.value.selectedTaskIds)
    }

    private fun viewModel(repository: FakeTaskRepository) = HistoryViewModel(
        loadDecompositionHistoryUseCase = LoadDecompositionHistoryUseCase(repository),
        deleteTaskUseCase = DeleteTaskUseCase(repository),
        clock = clock
    )

    /**
     * Преобразует ISO-дату в timestamp для подготовки тестовых задач.
     */
    private fun timestamp(value: String): Long =
        java.time.Instant.parse(value).toEpochMilli()

    /**
     * Дожидается выполнения асинхронной операции ViewModel до наступления ожидаемого состояния.
     */
    private suspend fun waitUntil(predicate: () -> Boolean) {
        withTimeout(1_000) {
            while (!predicate()) {
                delay(1)
            }
        }
    }
}
