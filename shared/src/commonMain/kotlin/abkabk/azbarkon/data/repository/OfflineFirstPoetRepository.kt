package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.datasource.PoetLocalDataSource
import abkabk.azbarkon.domain.model.Poet
import abkabk.azbarkon.domain.model.PoetWithCategories
import abkabk.azbarkon.domain.model.PoetWithRootCategories
import abkabk.azbarkon.domain.repository.PoetRepository

class OfflineFirstPoetRepository(
    private val localDataSource: PoetLocalDataSource,
) : PoetRepository {
    override suspend fun getPoets(): Result<List<Poet>, DataError.Local> =
        localDataSource.getPoets()

    override suspend fun getPoetsWithRootCategories(): Result<List<PoetWithRootCategories>, DataError.Local> =
        localDataSource.getPoetsWithRootCategories()

    override suspend fun getPoetWithCategories(poetId: Int): Result<PoetWithCategories, DataError.Local> =
        localDataSource.getPoetWithCategories(poetId)
}
