package abkabk.azbarkon.features.poets.navigation

import kotlinx.serialization.Serializable

@Serializable
data object PoetsListRoute

@Serializable
data class PoetDetailRoute(
    val poetId: Int,
)
