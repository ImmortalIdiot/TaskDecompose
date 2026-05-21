package io.ii.domain.repository

import io.ii.domain.model.LlmSettings
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий настроек языковой модели.
 *
 * Определяет контракт для чтения, получения обновлений и сохранения выбранного провайдера
 * и параметров подключения к API модели.
 */
interface LlmSettingsRepository {

    /**
     * Возвращает поток изменений настроек модели.
     *
     * @return поток актуальных настроек модели
     */
    fun observeSettings(): Flow<LlmSettings>

    /**
     * Возвращает текущие сохранённые настройки модели.
     *
     * @return актуальные настройки модели
     */
    suspend fun getSettings(): LlmSettings

    /**
     * Сохраняет настройки модели.
     *
     * @param settings настройки, которые нужно сохранить
     */
    suspend fun saveSettings(settings: LlmSettings)
}
