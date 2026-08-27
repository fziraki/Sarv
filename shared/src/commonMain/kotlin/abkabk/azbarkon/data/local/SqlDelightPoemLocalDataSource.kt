package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.domain.result.dbQuery
import abkabk.azbarkon.data.mapper.toMyPoemSummary
import abkabk.azbarkon.data.mapper.toPoemDetail
import abkabk.azbarkon.data.mapper.toPoemSummary
import abkabk.azbarkon.data.mapper.toPoemVerses
import abkabk.azbarkon.domain.datasource.PoemLocalDataSource
import abkabk.azbarkon.domain.model.MyPoemSummary
import abkabk.azbarkon.domain.model.PoemDetail
import abkabk.azbarkon.domain.model.PoemSummary
import com.sarv.db.PoemQueries
import com.sarv.db.VerseQueries

class SqlDelightPoemLocalDataSource(
    private val poemQueries: PoemQueries,
    private val verseQueries: VerseQueries,
) : PoemLocalDataSource {
    override suspend fun getPoemsByCatIdPage(
        catId: Int,
        offset: Int,
        limit: Int,
    ): Result<List<PoemSummary>, DataError.Local> =
        dbQuery {
            poemQueries
                .selectByCatIdPaged(
                    cat_id = catId.toLong(),
                    limit = limit.toLong(),
                    offset = offset.toLong(),
                ).executeAsList()
                .map { it.toPoemSummary() }
        }

    override suspend fun getPoemsByIds(ids: Set<Int>): Result<List<MyPoemSummary>, DataError.Local> =
        if (ids.isEmpty()) {
            Result.Success(emptyList())
        } else {
            dbQuery {
                poemQueries
                    .selectByIds(id = ids.map { it.toLong() })
                    .executeAsList()
                    .map { it.toMyPoemSummary() }
            }
        }

    override suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local> =
        dbQuery {
            val detail =
                poemQueries
                    .selectDetailById(id = poemId.toLong())
                    .executeAsOneOrNull()
                    ?: return Result.Error(DataError.Local.NOT_FOUND)

            val verses =
                verseQueries
                    .selectByPoemId(poem_id = poemId.toLong())
                    .executeAsList()
                    .toPoemVerses(poemId)

            detail.toPoemDetail(poemId = poemId, verses = verses)
        }
}
