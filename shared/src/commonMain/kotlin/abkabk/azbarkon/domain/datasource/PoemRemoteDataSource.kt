package abkabk.azbarkon.domain.datasource

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.model.PoemAudioTrack

interface PoemRemoteDataSource {

    suspend fun getPoemRecitations(poemId: Int): Result<List<PoemAudioTrack>, DataError.Network>
}
