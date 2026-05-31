package abkabk.azbarkon.features.poets

import abkabk.azbarkon.data.mapper.worksSummary
import abkabk.azbarkon.domain.model.PoetWithWorks
import abkabk.azbarkon.features.poets.components.workAccentColor
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color

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

@Stable
data class PoetWorkItemUi(
    val id: Int,
    val title: String,
    val subtitle: String,
    val accentColor: Color,
)

fun PoetWithWorks.toListItemUi(): PoetListItemUi =
    PoetListItemUi(
        id = poet.id ?: 0,
        name = poet.name.orEmpty(),
        worksSummary = worksSummary(works),
        imageUrl = poet.imageUrl,
    )

fun PoetWithWorks.toFeaturedPoetUi(): FeaturedPoetUi =
    FeaturedPoetUi(
        id = poet.id ?: 0,
        name = poet.name.orEmpty(),
        description = poet.description?.take(80).orEmpty(),
        stats = worksSummary(works),
        imageUrl = poet.imageUrl,
    )

fun PoetWithWorks.toWorkItemsUi(): List<PoetWorkItemUi> =
    works.map { work ->
        PoetWorkItemUi(
            id = work.id,
            title = work.title,
            subtitle = work.subtitle.orEmpty(),
            accentColor = workAccentColor(work.title),
        )
    }
