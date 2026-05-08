package io.ii.presentation.core

import android.content.Context

/**
 * Предоставляет доступ к строковым ресурсам приложения.
 *
 * Используется для получения строк без прямой зависимости ViewModel от Context.
 */
internal interface ResourceProvider {

    /**
     * Возвращает строку по идентификатору ресурса.
     *
     * @param resId идентификатор строкового ресурса
     * @return строковое значение ресурса
     */
    fun getString(resId: Int): String
}

/**
 * Реализация [ResourceProvider].
 */
internal class ResourceProviderImpl(private val context: Context) : ResourceProvider {
    override fun getString(resId: Int): String = context.getString(resId)
}
