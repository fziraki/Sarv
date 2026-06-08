package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.paging.DEFAULT_PAGING_CONFIG
import abkabk.azbarkon.data.paging.SearchVersesPagingSource
import abkabk.azbarkon.domain.datasource.SearchLocalDataSource
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchHit
import abkabk.azbarkon.domain.repository.SearchRepository
import androidx.paging.Pager
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

class OfflineFirstSearchRepository(
    private val localDataSource: SearchLocalDataSource,
) : SearchRepository {
    override suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local> =
        localDataSource.getCatById(catId)

    override fun searchVerses(
        query: String,
        poetId: Int?,
        categoryIds: Set<Int>?,
    ): Flow<PagingData<SearchHit>> =
        Pager(
            config = DEFAULT_PAGING_CONFIG,
            pagingSourceFactory = {
                SearchVersesPagingSource(
                    localDataSource = localDataSource,
                    query = query,
                    poetId = poetId,
                    categoryIds = categoryIds,
                )
            },
        ).flow
}
