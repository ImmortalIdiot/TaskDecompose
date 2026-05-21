package io.ii.domain.usecase

import io.ii.domain.model.LlmSettings
import io.ii.domain.repository.LlmSettingsRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case для получения потока настроек языковой модели.
 *
 * Используется экранами, которым нужно реагировать на смену провайдера или модели.
 *
 * @property repository репозиторий настроек языковой модели
 */
class ObserveLlmSettingsUseCase(
    private val repository: LlmSettingsRepository
) {

    /**
     * Возвращает поток актуальных настроек модели.
     *
     * @return поток настроек языковой модели
     */
    operator fun invoke(): Flow<LlmSettings> = repository.observeSettings()
}
