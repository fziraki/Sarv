package abkabk.azbarkon.features.poets

import abkabk.azbarkon.data.mapper.rootCategoriesSummary
import abkabk.azbarkon.domain.model.PoetWithRootCategories
import androidx.compose.runtime.Stable

private const val FEATURED_POET_DESCRIPTION_MAX_LENGTH = 80

const val GHAZAL_CATEGORY = "غزلیات"

@Stable
data class PoetListItemUi(
    val id: Int,
    val name: String,
    val worksSummary: String,
    val imageUrl: String?,
    val canChat: Boolean,
)

@Stable
data class FeaturedPoetUi(
    val id: Int,
    val name: String,
    val description: String,
    val stats: String,
    val imageUrl: String?,
    val canChat: Boolean,
)

fun PoetWithRootCategories.toListItemUi(): PoetListItemUi =
    PoetListItemUi(
        id = poet.id ?: 0,
        name = poet.name.orEmpty(),
        worksSummary = rootCategoriesSummary(rootCategories),
        imageUrl = poet.imageUrl,
        canChat = rootCategories.any { it.text == GHAZAL_CATEGORY },
    )

fun PoetWithRootCategories.toFeaturedPoetUi(): FeaturedPoetUi =
    FeaturedPoetUi(
        id = poet.id ?: 0,
        name = poet.name.orEmpty(),
        description = poet.description?.take(FEATURED_POET_DESCRIPTION_MAX_LENGTH).orEmpty(),
        stats = rootCategoriesSummary(rootCategories),
        imageUrl = poet.imageUrl,
        canChat = rootCategories.any { it.text == GHAZAL_CATEGORY },
    )
