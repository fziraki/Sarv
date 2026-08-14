package abkabk.azbarkon.data.paging

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.paging.MIN_PAGE_LOAD_MILLIS
import abkabk.azbarkon.domain.datasource.SearchLocalDataSource
import abkabk.azbarkon.domain.model.SearchHit
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay

class SearchVersesPagingSource(
    private val localDataSource: SearchLocalDataSource,
    private val query: String,
    private val poetId: Int?,
    private val categoryIds: Set<Int>?,
) : PagingSource<Int, SearchHit>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchHit> {
        if (params is LoadParams.Append) {
            delay(MIN_PAGE_LOAD_MILLIS)
        }
        val offset = params.key ?: 0
        return when (
            val result =
                localDataSource.searchVersesPage(
                    query = query,
                    poetId = poetId,
                    categoryIds = categoryIds,
                    offset = offset,
                    limit = params.loadSize,
                )
        ) {
            is Result.Success -> {
                val hits = result.data
                LoadResult.Page(
                    data = hits,
                    prevKey = if (offset == 0) null else (offset - params.loadSize).coerceAtLeast(0),
                    nextKey = if (hits.size < params.loadSize) null else offset + hits.size,
                )
            }

            is Result.Error -> LoadResult.Error(PagingLoadException(result.error))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SearchHit>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(state.config.pageSize)
            ?: anchorPage.nextKey?.minus(state.config.pageSize)
    }
}
