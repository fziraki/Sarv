package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.datasource.SearchLocalDataSource
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchPage
import abkabk.azbarkon.domain.repository.SearchRepository

class OfflineFirstSearchRepository(
    private val localDataSource: SearchLocalDataSource,
) : SearchRepository {
    override suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local> =
        localDataSource.getCatById(catId)

    override suspend fun searchVerses(
        query: String,
        poetId: Int?,
        categoryIds: Set<Int>?,
        offset: Int,
        limit: Int,
    ): Result<SearchPage, DataError.Local> =
        localDataSource.searchVerses(
            query = query,
            poetId = poetId,
            categoryIds = categoryIds,
            offset = offset,
            limit = limit,
        )
}
