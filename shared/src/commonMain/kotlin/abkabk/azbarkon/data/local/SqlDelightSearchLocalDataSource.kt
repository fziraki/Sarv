package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.data.mapper.toCatNode
import abkabk.azbarkon.data.mapper.toSearchHit
import abkabk.azbarkon.domain.datasource.SearchLocalDataSource
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchPage
import com.azbarkon.db.CatQueries
import com.azbarkon.db.SearchQueries

class SqlDelightSearchLocalDataSource(
    private val searchQueries: SearchQueries,
    private val catQueries: CatQueries,
) : SearchLocalDataSource {
    override suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local> =
        try {
            val row =
                catQueries
                    .selectById(id = catId.toLong())
                    .executeAsOneOrNull()
                    ?: return Result.Error(DataError.Local.NOT_FOUND)
            Result.Success(row.toCatNode())
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }

    override suspend fun searchVerses(
        query: String,
        poetId: Int?,
        categoryIds: Set<Int>?,
        offset: Int,
        limit: Int,
    ): Result<SearchPage, DataError.Local> =
        try {
            val trimmedQuery = query.trim()
            if (trimmedQuery.isEmpty()) {
                return Result.Success(SearchPage(hits = emptyList(), totalCount = 0))
            }

            val filterPoet = if (poetId != null) 1L else 0L
            val filterCat = if (!categoryIds.isNullOrEmpty()) 1L else 0L
            val catIds = categoryIds?.map { it.toLong() } ?: listOf(0L)

            val totalCount =
                searchQueries
                    .countSearchVerses(
                        query = trimmedQuery,
                        filter_poet = filterPoet,
                        poet_id = poetId?.toLong() ?: 0L,
                        filter_cat = filterCat,
                        cat_ids = catIds,
                    ).executeAsOne()
                    .toInt()

            val hits =
                searchQueries
                    .searchVerses(
                        query = trimmedQuery,
                        filter_poet = filterPoet,
                        poet_id = poetId?.toLong() ?: 0L,
                        filter_cat = filterCat,
                        cat_ids = catIds,
                        limit = limit.toLong(),
                        offset = offset.toLong(),
                    ).executeAsList()
                    .map { it.toSearchHit() }

            Result.Success(SearchPage(hits = hits, totalCount = totalCount))
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
