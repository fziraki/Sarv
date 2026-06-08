package abkabk.azbarkon.features.search.navigation

import kotlinx.serialization.Serializable

@Serializable
data class SearchRoute(
    val poetId: Int? = null,
    val catId: Int? = null,
)
