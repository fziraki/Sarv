package abkabk.azbarkon.testing

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.paging.DEFAULT_PAGING_CONFIG
import abkabk.azbarkon.data.paging.PagingLoadException
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchHit
import abkabk.azbarkon.domain.repository.SearchRepository
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.Flow

class FakeSearchRepository : SearchRepository {
    var catsById: Map<Int, CatNode> = emptyMap()
    var searchPages: List<SearchHit> = emptyList()
    var shouldFailSearch: Boolean = false

    override suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local> {
        val cat = catsById[catId] ?: return Result.Error(DataError.Local.NOT_FOUND)
        return Result.Success(cat)
    }

    override fun searchVerses(
        query: String,
        poetId: Int?,
        categoryIds: Set<Int>?,
    ): Flow<PagingData<SearchHit>> =
        Pager(
            config = DEFAULT_PAGING_CONFIG,
            pagingSourceFactory = {
                object : PagingSource<Int, SearchHit>() {
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchHit> {
                        if (shouldFailSearch) {
                            return LoadResult.Error(PagingLoadException(DataError.Local.UNKNOWN))
                        }

                        val offset = params.key ?: 0
                        val hits = searchPages.drop(offset).take(params.loadSize)
                        return LoadResult.Page(
                            data = hits,
                            prevKey = if (offset == 0) null else (offset - params.loadSize).coerceAtLeast(0),
                            nextKey = if (hits.size < params.loadSize) null else offset + hits.size,
                        )
                    }

                    override fun getRefreshKey(state: PagingState<Int, SearchHit>): Int? {
                        val anchorPosition = state.anchorPosition ?: return null
                        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
                        return anchorPage.prevKey?.plus(state.config.pageSize)
                            ?: anchorPage.nextKey?.minus(state.config.pageSize)
                    }
                }
            },
        ).flow
}
