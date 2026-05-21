package io.ii.data.repository

import io.ii.data.local.model.LlmSettingsStorage
import io.ii.domain.model.LlmSettings
import io.ii.domain.repository.LlmSettingsRepository
import kotlinx.coroutines.flow.Flow

internal class LlmSettingsRepositoryImpl(
    private val storage: LlmSettingsStorage
) : LlmSettingsRepository {

    override suspend fun getSettings(): LlmSettings = storage.getSettings()

    override suspend fun saveSettings(settings: LlmSettings) {
        storage.saveSettings(settings)
    }
}
