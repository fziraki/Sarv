package abkabk.azbarkon.data.remote

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.domain.result.map
import abkabk.azbarkon.core.network.getResult
import abkabk.azbarkon.data.mapper.toPoemAudioTrack
import abkabk.azbarkon.data.remote.dto.RecitationDto
import abkabk.azbarkon.domain.datasource.PoemRemoteDataSource
import abkabk.azbarkon.domain.model.PoemAudioTrack
import io.ktor.client.HttpClient

class KtorPoemRemoteDataSource(
    private val httpClient: HttpClient,
) : PoemRemoteDataSource {

    override suspend fun getPoemRecitations(
        poemId: Int,
    ): Result<List<PoemAudioTrack>, DataError.Network> {
        return httpClient.getResult<List<RecitationDto>>(
            route = "/api/ganjoor/poem/$poemId/recitations",
        ).map {
            it.map { it.toPoemAudioTrack() }
        }.let { result ->
            if (result is Result.Error && result.error == DataError.Network.NOT_FOUND) {
                Result.Success(emptyList())
            } else {
                result
            }
        }

    }
}
