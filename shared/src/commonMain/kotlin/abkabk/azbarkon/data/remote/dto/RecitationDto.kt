package abkabk.azbarkon.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecitationDto(
    val id: Int,
    val audioTitle: String? = null,
    val audioArtist: String? = null,
    val audioArtistUrl: String? = null,
    val mp3Url: String? = null,
)
