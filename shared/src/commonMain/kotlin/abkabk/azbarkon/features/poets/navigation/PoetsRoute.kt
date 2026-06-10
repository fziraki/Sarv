package abkabk.azbarkon.features.poets.navigation

import kotlinx.serialization.Serializable

@Serializable
data class ChatRoute(
    val poetId: Int,
)

@Serializable
data object PoetsListRoute

@Serializable
data class PoetDetailRoute(
    val poetId: Int,
)

@Serializable
data class PoemListRoute(
    val catId: Int,
    val title: String,
)

@Serializable
data class PoemDetailRoute(
    val poemId: Int,
)
