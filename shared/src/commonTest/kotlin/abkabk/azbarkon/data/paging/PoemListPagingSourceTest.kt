package abkabk.azbarkon.data.paging

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.paging.PAGE_SIZE
import abkabk.azbarkon.domain.datasource.PoemLocalDataSource
import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary
import androidx.paging.PagingSource
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class PoemListPagingSourceTest {
    @Test
    fun `loads poems in pages`() =
        runTest {
            val dataSource =
                FakePoemLocalDataSource(
                    poemsByCat =
                        mapOf(
                            1 to (1..15).map { index -> PoemSummary(id = index, title = "Poem $index") },
                        ),
                )
            val pagingSource = PoemListPagingSource(dataSource, catId = 1)

            val firstPage =
                pagingSource.load(
                    PagingSource.LoadParams.Refresh(
                        key = null,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                ) as PagingSource.LoadResult.Page

            assertThat(firstPage.data).hasSize(PAGE_SIZE)
            assertThat(firstPage.prevKey).isNull()
            assertThat(firstPage.nextKey).isEqualTo(PAGE_SIZE)

            val secondPage =
                pagingSource.load(
                    PagingSource.LoadParams.Append(
                        key = PAGE_SIZE,
                        loadSize = PAGE_SIZE,
                        placeholdersEnabled = false,
                    ),
                ) as PagingSource.LoadResult.Page

            assertThat(secondPage.data).hasSize(5)
            assertThat(secondPage.nextKey).isNull()
        }

    @Test
    fun `returns error when data source fails`() =
        runTest {
            val dataSource =
                FakePoemLocalDataSource(
                    shouldFail = true,
                )
            val pagingSource = PoemListPagingSource(dataSource, catId = 1)

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

    private class FakePoemLocalDataSource(
        private val poemsByCat: Map<Int, List<PoemSummary>> = emptyMap(),
        private val shouldFail: Boolean = false,
    ) : PoemLocalDataSource {
        override suspend fun getPoemsByCatIdPage(
            catId: Int,
            offset: Int,
            limit: Int,
        ): Result<List<PoemSummary>, DataError.Local> =
            if (shouldFail) {
                Result.Error(DataError.Local.UNKNOWN)
            } else {
                Result.Success(
                    poemsByCat[catId].orEmpty().drop(offset).take(limit),
                )
            }

        override suspend fun getPoemsByIds(ids: Set<Int>): Result<List<MyPoemSummary>, DataError.Local> =
            Result.Success(emptyList())

        override suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local> =
            Result.Error(DataError.Local.NOT_FOUND)
    }
}
