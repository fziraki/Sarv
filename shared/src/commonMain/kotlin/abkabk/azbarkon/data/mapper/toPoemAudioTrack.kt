package abkabk.azbarkon.data.mapper

import abkabk.azbarkon.data.remote.dto.RecitationDto
import abkabk.azbarkon.domain.model.PoemAudioTrack

fun RecitationDto.toPoemAudioTrack(): PoemAudioTrack = PoemAudioTrack(
    id = id,
    title = audioTitle.orEmpty(),
    artist = audioArtist.orEmpty(),
    artistUrl = audioArtistUrl,
    url = mp3Url,
)