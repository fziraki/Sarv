package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.Poet

/** Local SQLDelight rows only store id and name; network-only fields stay null. */
fun com.azbarkon.db.Poet.toPoet(): Poet =
    Poet(
        id = id.toInt(),
        name = name,
        description = null,
        rootCatId = null,
        imageUrl = null,
    )
