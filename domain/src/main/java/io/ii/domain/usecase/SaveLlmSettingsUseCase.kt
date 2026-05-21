package io.ii.domain.usecase

import io.ii.domain.model.LlmSettings
import io.ii.domain.repository.LlmSettingsRepository

/**
 * Use case для сохранения настроек языковой модели.
 *
 * Делегирует сохранение выбранного провайдера и параметров API в репозиторий настроек.
 *
 * @property repository репозиторий настроек языковой модели
 */
class SaveLlmSettingsUseCase(
    private val repository: LlmSettingsRepository
) : UseCase<LlmSettings, Unit>() {

    /**
     * Сохраняет настройки модели.
     *
     * @param params настройки модели
     */
    override suspend fun execute(params: LlmSettings) {
        repository.saveSettings(params)
    }
}
