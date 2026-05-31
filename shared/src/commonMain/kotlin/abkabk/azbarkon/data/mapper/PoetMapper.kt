package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.Poet

object PoetMapper {
    /** Local SQLDelight rows only store id and name; network-only fields stay null. */
    fun fromEntity(entity: com.azbarkon.db.Poet): Poet =
        Poet(
            id = entity.id.toInt(),
            name = entity.name,
            description = null,
            rootCatId = null,
            imageUrl = null,
        )
}
