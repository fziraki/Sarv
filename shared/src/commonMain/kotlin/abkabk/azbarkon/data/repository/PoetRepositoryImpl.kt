package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.network.ApiResult
import abkabk.azbarkon.core.network.safeApiCall
import abkabk.azbarkon.data.local.PoetLocalDataSource
import abkabk.azbarkon.data.remote.PoetApi
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.repository.PoetRepository

class PoetRepositoryImpl(
    private val api: PoetApi,
    private val poetLocalDataSource: PoetLocalDataSource,
) : PoetRepository {
    override suspend fun getPoets(): ApiResult<List<Poet>> =
        safeApiCall {
            api.getPoetList().map { it.toDomain() }
        }

    override suspend fun getPoetsLocally(): List<Poet> = poetLocalDataSource.getAllPoets()
}
