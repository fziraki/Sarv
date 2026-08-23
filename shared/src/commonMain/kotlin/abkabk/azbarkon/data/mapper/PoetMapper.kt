package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.core.util.Constants
import abkabk.azbarkon.domain.model.Poet

fun buildPoetImageUrl(catUrl: String?): String? {
    val slug =
        catUrl
            ?.trim('/')
            ?.substringAfterLast('/')
            ?.takeIf { it.isNotBlank() }
    return slug?.let { "${Constants.BASE_URL}api/ganjoor/poet/image/$it.png" }
}

fun com.azbarkon.db.SelectAllWithCatUrl.toPoet(): Poet =
    Poet(
        id = id.toInt(),
        name = name,
        description = description,
        rootCatId = cat_id.toInt(),
        imageUrl = buildPoetImageUrl(cat_url),
        isDownloaded = is_downloaded,
    )

fun com.azbarkon.db.SelectByIdWithCatUrl.toPoet(): Poet =
    Poet(
        id = id.toInt(),
        name = name,
        description = description,
        rootCatId = cat_id.toInt(),
        imageUrl = buildPoetImageUrl(cat_url),
        isDownloaded = is_downloaded,
    )
