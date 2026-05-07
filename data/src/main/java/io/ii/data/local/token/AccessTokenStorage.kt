package io.ii.data.local.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.ii.data.remote.dto.GigaChatAccessToken
import kotlinx.coroutines.flow.first

/**
 * Локальное хранилище токена доступа GigaChat API.
 *
 * Использует DataStore Preferences для сохранения токена авторизации и времени его истечения.
 *
 * Предоставляет методы для чтения, сохранения и удаления токена.
 */
internal class AccessTokenStorage(
    private val context: Context
) {

    suspend fun saveToken(token: GigaChatAccessToken) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token.accessToken
            preferences[EXPIRES_AT_KEY] = token.expiresAt
        }
    }

    suspend fun getToken(): GigaChatAccessToken? {
        val preferences = context.dataStore.data.first()

        val accessToken = preferences[ACCESS_TOKEN_KEY]
        val expiresAt = preferences[EXPIRES_AT_KEY]

        return if (accessToken != null && expiresAt != null) {
            GigaChatAccessToken(
                accessToken = accessToken,
                expiresAt = expiresAt
            )
        } else {
            null
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(EXPIRES_AT_KEY)
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "gigachat_token"

        private val Context.dataStore by preferencesDataStore(name = PREFERENCES_NAME)

        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val EXPIRES_AT_KEY = longPreferencesKey("expires_at")
    }
}