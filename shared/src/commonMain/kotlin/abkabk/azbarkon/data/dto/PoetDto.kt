package abkabk.azbarkon.data.dto

import abkabk.azbarkon.domain.model.Poet
import kotlinx.serialization.Serializable

@Serializable
data class PoetDto(
    val id: Int?,
    val name: String?,
    val description: String?,
    val rootCatId: Int?,
    val imageUrl: String?,
) {
    fun toDomain(): Poet =
        Poet(
            id = id,
            name = name,
            description = description,
            rootCatId = rootCatId,
            imageUrl = imageUrl,
        )
}
