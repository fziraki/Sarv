package abkabk.azbarkon.features.tasvirNegar.navigation

import kotlinx.serialization.Serializable

@Serializable
data class TasvirNegarRoute(
    val poemId: Int? = null,
    val initialText: String? = null,
)
