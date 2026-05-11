package io.ii.presentation.viewmodels

import io.ii.domain.model.Task
import io.ii.domain.usecase.LoadDecompositionHistoryUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
/**
 * Проверяет бизнес-логику экрана истории на уровне ViewModel.
 *
 * Эти тесты фиксируют сортировку задач и группировку по дате создания.
 */
class HistoryViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Проверяет, что история сортируется от новых задач к старым
     * и группируется по календарной дате создания.
     */
    @Test
    fun `loadHistory sorts tasks by creation time and groups by date`() {
        val repository = FakeTaskRepository()
        val viewModel = HistoryViewModel(LoadDecompositionHistoryUseCase(repository))

        repository.emitHistory(
            listOf(
                Task("older", "Older", null, timestamp("2026-01-01T12:00:00Z")),
                Task("newer", "Newer", null, timestamp("2026-01-02T09:00:00Z")),
                Task("sameDay", "Same day", null, timestamp("2026-01-02T08:00:00Z"))
            )
        )

        val state = viewModel.uiState.value

        assertFalse(state.isLoading)
        assertEquals(listOf("02.01.2026", "01.01.2026"), state.groups.map { it.date })
        assertEquals(listOf("newer", "sameDay"), state.groups.first().tasks.map { it.id })
    }

    /**
     * Преобразует ISO-дату в timestamp для подготовки тестовых задач.
     */
    private fun timestamp(value: String): Long =
        java.time.Instant.parse(value).toEpochMilli()
}
