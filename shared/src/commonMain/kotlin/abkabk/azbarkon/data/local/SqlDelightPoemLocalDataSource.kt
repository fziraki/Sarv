package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
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
import io.github.aakira.napier.Napier

class SqlDelightPoemLocalDataSource(
    private val poemQueries: PoemQueries,
    private val verseQueries: VerseQueries,
) : PoemLocalDataSource {
    @Suppress("TooGenericExceptionCaught", "MagicNumber")
    override suspend fun getPoemsByCatIdPage(
        catId: Int,
        offset: Int,
        limit: Int,
    ): Result<List<PoemSummary>, DataError.Local> =
        try {
            poemQueries
                .selectByCatIdPaged(
                    cat_id = catId.toLong(),
                    limit = limit.toLong(),
                    offset = offset.toLong(),
                ).executeAsList()
                .map { it.toPoemSummary() }
                .also { page ->
                    Napier.d(
                        message = "cat=$catId offset=$offset ids=${page.take(10).map { it.id }}",
                        tag = "PoemDebug",
                    )
                }
                .let { Result.Success(it) }
        } catch (e: Exception) {
            Napier.e(message = "getPoemsByCatIdPage failed: ${e.message}", throwable = e, tag = "PoemDebug")
            Result.Error(DataError.Local.QUERY_FAILED)
        }

    override suspend fun getPoemsByIds(ids: Set<Int>): Result<List<MyPoemSummary>, DataError.Local> =
        if (ids.isEmpty()) {
            Result.Success(emptyList())
        } else {
            try {
                Result.Success(
                    poemQueries
                        .selectByIds(id = ids.map { it.toLong() })
                        .executeAsList()
                        .map { it.toMyPoemSummary() },
                )
            } catch (e: Exception) {
                Napier.e("getPoemsByIds failed", e)
                Result.Error(DataError.Local.QUERY_FAILED)
            }
        }

    override suspend fun getPoemDetail(poemId: Int): Result<PoemDetail, DataError.Local> =
        try {
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

            Result.Success(detail.toPoemDetail(poemId = poemId, verses = verses))
        } catch (e: Exception) {
            Napier.e("getPoemDetail failed for poemId=$poemId", e)
            Result.Error(DataError.Local.QUERY_FAILED)
        }
}
