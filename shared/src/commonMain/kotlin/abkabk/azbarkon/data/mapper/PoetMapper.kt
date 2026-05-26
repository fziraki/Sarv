package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.domain.model.Poet

object PoetMapper {

    fun fromEntity(entity: com.azbarkon.db.Poet): Poet {
        return Poet(
            id = entity.id.toInt(),
            name = entity.name,
            description = null,
            rootCatId = null,
            imageUrl = null
        )
    }
}