package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.paging.DEFAULT_PAGING_CONFIG
import abkabk.azbarkon.data.paging.PagingLoadException
import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary
import abkabk.azbarkon.domain.repository.PoemRepository
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow

class FakePoemRepository : PoemRepository {
    var poemsByCatId: Map<Int, List<PoemSummary>> = emptyMap()
    var poemsByIds: Map<Int, MyPoemSummary> = emptyMap()
    var poemDetails: Map<Int, PoemDetail> = emptyMap()
    var shouldFailPaging: Boolean = false
    var shouldFailByIds: Boolean = false
    var shouldFailDetail: Boolean = false

    override fun poemsByCatId(catId: Int): Flow<PagingData<PoemSummary>> =
        Pager(
            config = DEFAULT_PAGING_CONFIG,
            pagingSourceFactory = {
                object : PagingSource<Int, PoemSummary>() {
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PoemSummary> {
                        if (shouldFailPaging) {
                            return LoadResult.Error(PagingLoadException(DataError.Local.UNKNOWN))
                        }

                        val allPoems = poemsByCatId[catId].orEmpty()
                        val offset = params.key ?: 0
                        val page = allPoems.drop(offset).take(params.loadSize)
                        return LoadResult.Page(
                            data = page,
                            prevKey = if (offset == 0) null else (offset - params.loadSize).coerceAtLeast(0),
                            nextKey = if (page.size < params.loadSize) null else offset + page.size,
                        )
                    }

                    override fun getRefreshKey(state: PagingState<Int, PoemSummary>): Int? {
                        val anchorPosition = state.anchorPosition ?: return null
                        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
                        return anchorPage.prevKey?.plus(state.config.pageSize)
                            ?: anchorPage.nextKey?.minus(state.config.pageSize)
                    }
                }
            },
        ).flow

    override suspend fun getPoemsByIds(ids: Set<Int>): Result<List<MyPoemSummary>, DataError.Local> =
        if (shouldFailByIds) {
            Result.Error(DataError.Local.UNKNOWN)
        } else if (ids.isEmpty()) {
            Result.Success(emptyList())
        } else {
            Result.Success(
                ids.mapNotNull { poemsByIds[it] },
            )
        }

    override suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local> =
        if (shouldFailDetail) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            poemDetails[poemId]?.let { Result.Success(it) }
                ?: Result.Error(DataError.Local.NOT_FOUND)
        }
}
