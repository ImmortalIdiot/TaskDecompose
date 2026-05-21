package io.ii.data.model

import io.ii.data.remote.api.custom.CustomModelApi
import io.ii.data.remote.api.gigachat.GigaChatApi
import io.ii.data.remote.dto.DecompositionApiResult
import io.ii.domain.model.LlmSettings
import io.ii.domain.repository.LlmSettingsRepository
import io.ktor.client.HttpClient

/**
 * Маршрутизирует запрос декомпозиции в выбранный пользователем API модели.
 *
 * Перед каждым запросом читает актуальные настройки модели и выбирает
 * соответствующий клиент: GigaChat или пользовательский API.
 */
internal class LlmRouter(
    private val client: HttpClient,
    private val gigaChatApi: GigaChatApi,
    private val settingsRepository: LlmSettingsRepository
) {

    /**
     * Выполняет декомпозицию через выбранного провайдера модели.
     *
     * @param prompt промпт для модели
     * @return результат запроса к выбранному API
     */
    suspend fun decomposeTask(prompt: String): DecompositionApiResult {
        val settings = settingsRepository.getSettings()

        return if (settings.selectedModelId == LlmSettings.GIGACHAT_MODEL_ID) {
            gigaChatApi.decomposeTask(
                prompt = prompt,
                model = settings.gigaChatModel
            )
        } else {
            val customModel = settings.customModels.firstOrNull { model ->
                model.id == settings.selectedModelId
            } ?: error("This custom model not found")

            CustomModelApi(client, customModel).decomposeTask(prompt)
        }
    }
}
