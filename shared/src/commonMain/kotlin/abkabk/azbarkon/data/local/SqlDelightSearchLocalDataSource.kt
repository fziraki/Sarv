package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.domain.result.dbQuery
import abkabk.azbarkon.data.mapper.toCatNode
import abkabk.azbarkon.data.mapper.toSearchHit
import abkabk.azbarkon.domain.datasource.SearchLocalDataSource
import abkabk.azbarkon.domain.model.CatNode
import abkabk.azbarkon.domain.model.SearchHit
import com.sarv.db.CatQueries
import com.sarv.db.SearchQueries

class SqlDelightSearchLocalDataSource(
    private val searchQueries: SearchQueries,
    private val catQueries: CatQueries,
) : SearchLocalDataSource {
    override suspend fun getCatById(catId: Int): Result<CatNode, DataError.Local> =
        dbQuery {
            catQueries
                .selectById(id = catId.toLong())
                .executeAsOneOrNull()
                ?.toCatNode()
                ?: return Result.Error(DataError.Local.NOT_FOUND)
        }

    override suspend fun searchVersesPage(
        query: String,
        poetId: Int?,
        categoryIds: Set<Int>?,
        offset: Int,
        limit: Int,
    ): Result<List<SearchHit>, DataError.Local> =
        dbQuery {
            val ftsQuery = buildFtsQuery(query)
            if (ftsQuery.isEmpty()) {
                return Result.Success(emptyList())
            }

            val filterPoet = if (poetId != null) 1L else 0L
            val filterCat = if (!categoryIds.isNullOrEmpty()) 1L else 0L
            val catIds = categoryIds?.map { it.toLong() } ?: listOf(0L)

            searchQueries
                .searchVerses(
                    query = ftsQuery,
                    filter_poet = filterPoet,
                    poet_id = poetId?.toLong() ?: 0L,
                    filter_cat = filterCat,
                    cat_ids = catIds,
                    limit = limit.toLong(),
                    offset = offset.toLong(),
                ).executeAsList()
                .map { it.toSearchHit() }
        }

    private fun buildFtsQuery(raw: String): String {
        val normalized =
            raw
                .replace("\u200c", "")
                .replace("\u0640", "")
                .filterNot { it in '\u064b'..'\u0652' || it == '\u0670' }
                .replace('\u064a', '\u06cc')
                .replace('\u0643', '\u06a9')
                .replace('\u0623', '\u0627')
                .replace('\u0625', '\u0627')
                .replace('\u0622', '\u0627')
                .replace('\u0629', '\u0647')
        val tokens = normalized.split(Regex("\\s+")).filter { it.isNotBlank() }
        if (tokens.isEmpty()) return ""
        return tokens.mapIndexed { index, token ->
            val escaped = token.replace("\"", "\"\"")
            if (index == tokens.lastIndex) "\"$escaped\"*" else "\"$escaped\""
        }.joinToString(" ")
    }
}
