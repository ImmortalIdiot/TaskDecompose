package io.ii.data.remote.api

import io.ii.data.remote.dto.SubtaskDto
import io.ktor.client.HttpClient

internal class GigaChatApi(
    private val client: HttpClient
) {
    suspend fun decomposeTask(prompt: String): List<SubtaskDto> {
        TODO("Call giga chat api endpoint")
    }

    suspend fun authorize(): String {
        TODO("Call auth endpoint for getting bearer token. Save token to private storage and refresh it after 30 minutes.")
    }
}
