package abkabk.azbarkon.app.data.repository

import abkabk.azbarkon.app.core.network.ApiResult
import abkabk.azbarkon.app.core.network.safeApiCall
import abkabk.azbarkon.app.data.remote.PoetApi
import abkabk.azbarkon.app.domain.model.Poet
import abkabk.azbarkon.app.domain.repository.PoetRepository

class PoetRepositoryImpl(
    private val api: PoetApi
) : PoetRepository {

    override suspend fun getPoets(): ApiResult<List<Poet>> {
        return safeApiCall {
            api.getPoetList().map { it.toDomain() }
        }
    }
}