package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.datasource.PoetLocalDataSource
import abkabk.azbarkon.domain.datasource.PoetRemoteDataSource
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.repository.PoetRepository

class OfflineFirstPoetRepository(
    private val localDataSource: PoetLocalDataSource,
    private val remoteDataSource: PoetRemoteDataSource,
) : PoetRepository {
    override suspend fun getPoets(): Result<List<Poet>, DataError> =
        when (val result = remoteDataSource.fetchPoets()) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(result.error)
        }

    override suspend fun getPoetsLocally(): Result<List<Poet>, DataError.Local> =
        localDataSource.getPoets()
}
