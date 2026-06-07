package abkabk.azbarkon.features.poets

import abkabk.azbarkon.data.mapper.rootCategoriesSummary
import abkabk.azbarkon.domain.model.PoetWithRootCategories
import androidx.compose.runtime.Stable

@Stable
data class PoetListItemUi(
    val id: Int,
    val name: String,
    val worksSummary: String,
    val imageUrl: String?,
)

@Stable
data class FeaturedPoetUi(
    val id: Int,
    val name: String,
    val description: String,
    val stats: String,
    val imageUrl: String?,
)

fun PoetWithRootCategories.toListItemUi(): PoetListItemUi =
    PoetListItemUi(
        id = poet.id ?: 0,
        name = poet.name.orEmpty(),
        worksSummary = rootCategoriesSummary(rootCategories),
        imageUrl = poet.imageUrl,
    )

fun PoetWithRootCategories.toFeaturedPoetUi(): FeaturedPoetUi =
    FeaturedPoetUi(
        id = poet.id ?: 0,
        name = poet.name.orEmpty(),
        description = poet.description?.take(80).orEmpty(),
        stats = rootCategoriesSummary(rootCategories),
        imageUrl = poet.imageUrl,
    )
