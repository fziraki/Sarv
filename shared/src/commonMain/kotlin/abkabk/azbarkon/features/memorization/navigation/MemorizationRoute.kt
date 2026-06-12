package abkabk.azbarkon.features.memorization.navigation

import kotlinx.serialization.Serializable

@Serializable
data object MemorizationSelectRoute

@Serializable
data object ActiveMemorizationRoute

@Serializable
data class MemorizationPracticeRoute(
    val poemId: Int? = null,
)
