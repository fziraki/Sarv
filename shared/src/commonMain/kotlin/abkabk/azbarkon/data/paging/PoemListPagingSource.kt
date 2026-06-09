package abkabk.azbarkon.data.paging

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.domain.datasource.PoemLocalDataSource
import abkabk.azbarkon.domain.model.PoemSummary
import androidx.paging.PagingSource
import androidx.paging.PagingState

class PoemListPagingSource(
    private val localDataSource: PoemLocalDataSource,
    private val catId: Int,
) : PagingSource<Int, PoemSummary>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PoemSummary> {
        val offset = params.key ?: 0
        return when (
            val result =
                localDataSource.getPoemsByCatIdPage(
                    catId = catId,
                    offset = offset,
                    limit = params.loadSize,
                )
        ) {
            is Result.Success -> {
                val poems = result.data
                LoadResult.Page(
                    data = poems,
                    prevKey = if (offset == 0) null else (offset - params.loadSize).coerceAtLeast(0),
                    nextKey = if (poems.size < params.loadSize) null else offset + poems.size,
                )
            }

            is Result.Error -> LoadResult.Error(PagingLoadException(result.error))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, PoemSummary>): Int? {
        val anchorPosition = state.anchorPosition ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition) ?: return null
        return anchorPage.prevKey?.plus(state.config.pageSize)
            ?: anchorPage.nextKey?.minus(state.config.pageSize)
    }
}
