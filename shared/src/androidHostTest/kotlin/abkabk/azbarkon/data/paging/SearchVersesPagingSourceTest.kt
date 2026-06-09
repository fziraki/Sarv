package abkabk.azbarkon.data.paging

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.paging.PAGE_SIZE
import abkabk.azbarkon.domain.datasource.SearchLocalDataSource
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchHit
import androidx.paging.PagingSource
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class SearchVersesPagingSourceTest {
    @Test
    fun `forwards query and filters to data source`() =
        runTest {
            val dataSource = RecordingSearchLocalDataSource()
            val pagingSource =
                SearchVersesPagingSource(
                    localDataSource = dataSource,
                    query = "بیت",
                    poetId = 2,
                    categoryIds = setOf(24),
                )

            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = PAGE_SIZE,
                    placeholdersEnabled = false,
                ),
            )

            assertThat(dataSource.lastQuery).isEqualTo("بیت")
            assertThat(dataSource.lastPoetId).isEqualTo(2)
            assertThat(dataSource.lastCategoryIds).isEqualTo(setOf(24))
            assertThat(dataSource.lastOffset).isEqualTo(0)
            assertThat(dataSource.lastLimit).isEqualTo(PAGE_SIZE)
        }

    @Test
    fun `loads search hits in pages`() =
        runTest {
            val hits =
                (1..12).map { index ->
                    SearchHit(
                        poemId = index,
                        poemTitle = "Poem $index",
                        poetName = "حافظ",
                        categoryName = "غزلیات",
                        verseText = "بیت $index",
                        verseOrder = 1,
                    )
                }
            val dataSource = RecordingSearchLocalDataSource(hits = hits)
            val pagingSource =
                SearchVersesPagingSource(
                    localDataSource = dataSource,
                    query = "بیت",
                    poetId = null,
                    categoryIds = null,
                )

            val firstPage =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                ) as PagingSource.LoadResult.Page

            assertThat(firstPage.data).hasSize(PAGE_SIZE)

            val secondPage =
                pagingSource.load(
                    PagingSource.LoadParams.Append(
                        key = PAGE_SIZE,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                ) as PagingSource.LoadResult.Page

            assertThat(secondPage.data).hasSize(2)
        }

    @Test
    fun `returns error when data source fails`() =
        runTest {
            val dataSource = RecordingSearchLocalDataSource(shouldFail = true)
            val pagingSource =
                SearchVersesPagingSource(
                    localDataSource = dataSource,
                    query = "بیت",
                    poetId = null,
                    categoryIds = null,
                )

            val result =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                )

            assertThat(result).isInstanceOf(PagingSource.LoadResult.Error::class)
        }

    private class RecordingSearchLocalDataSource(
        private val hits: List<SearchHit> = emptyList(),
        private val shouldFail: Boolean = false,
    ) : SearchLocalDataSource {
        var lastQuery: String? = null
        var lastPoetId: Int? = null
        var lastCategoryIds: Set<Int>? = null
        var lastOffset: Int? = null
        var lastLimit: Int? = null

        override suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local> =
            Result.Error(DataError.Local.NOT_FOUND)

        override suspend fun searchVersesPage(
            query: String,
            poetId: Int?,
            categoryIds: Set<Int>?,
            offset: Int,
            limit: Int,
        ): Result<List<SearchHit>, DataError.Local> {
            lastQuery = query
            lastPoetId = poetId
            lastCategoryIds = categoryIds
            lastOffset = offset
            lastLimit = limit
            return if (shouldFail) {
                Result.Error(DataError.Local.UNKNOWN)
            } else {
                Result.Success(hits.drop(offset).take(limit))
            }
        }
    }
}
