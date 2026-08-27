package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.data.mapper.toRandomDistich
import abkabk.azbarkon.domain.datasource.DailyBeytLocalDataSource
import abkabk.azbarkon.domain.model.RandomDistich
import com.sarv.db.VerseQueries
import io.github.aakira.napier.Napier

class SqlDelightDailyBeytLocalDataSource(
    private val verseQueries: VerseQueries,
) : DailyBeytLocalDataSource {
    override suspend fun getRandomDistich(
        seed: Long,
        poetId: Int,
    ): Result<RandomDistich, DataError.Local> =
        try {
            val filterPoet = if (poetId == 0) 0L else 1L
            val poetIdParam = poetId.toLong()
            val count =
                verseQueries
                    .selectRandomDistichCountByPoet(
                        filter_poet = filterPoet,
                        poet_id = poetIdParam,
                    ).executeAsOne()
            if (count == 0L) {
                Result.Error(DataError.Local.NOT_FOUND)
            } else {
                val offset = (seed and Long.MAX_VALUE) % count
                val distich =
                    verseQueries
                        .selectRandomDistichAtOffsetByPoet(
                            filter_poet = filterPoet,
                            poet_id = poetIdParam,
                            offset = offset,
                        ).executeAsOneOrNull()
                        ?.toRandomDistich()
                if (distich == null) {
                    Result.Error(DataError.Local.NOT_FOUND)
                } else {
                    Result.Success(distich)
                }
            }
        } catch (e: Exception) {
            Napier.e("getRandomDistich failed", e)
            Result.Error(DataError.Local.QUERY_FAILED)
        }
}
