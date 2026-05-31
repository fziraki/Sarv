package abkabk.azbarkon.data.remote

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.domain.result.map
import abkabk.azbarkon.core.network.safeCall
import abkabk.azbarkon.data.dto.PoetDto
import abkabk.azbarkon.domain.datasource.PoetRemoteDataSource
import abkabk.azbarkon.domain.model.Poet
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header

class KtorPoetRemoteDataSource(
    private val client: HttpClient,
) : PoetRemoteDataSource {
    override suspend fun fetchPoets(): Result<List<Poet>, DataError.Network> =
        safeCall<List<PoetDto>> {
            client.get("api/ganjoor/poets") {
                header("isAuthorizable", "false")
                header("cacheSeconds", 60 * 60 * 24)
            }
        }.map { poets -> poets.map(PoetDto::toDomain) }
}
