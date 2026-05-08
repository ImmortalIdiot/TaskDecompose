package io.ii.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface Route {

    @Serializable
    data object TaskEditor : Route

    @Serializable
    data object History : Route
}
