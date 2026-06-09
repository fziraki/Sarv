package abkabk.azbarkon.data.local

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.data.mapper.toRandomDistich
import abkabk.azbarkon.domain.datasource.DailyBeytLocalDataSource
import abkabk.azbarkon.domain.model.RandomDistich
import com.azbarkon.db.VerseQueries

class SqlDelightDailyBeytLocalDataSource(
    private val verseQueries: VerseQueries,
) : DailyBeytLocalDataSource {
    override suspend fun getRandomDistich(seed: Long): Result<RandomDistich, DataError.Local> =
        try {
            val count = verseQueries.selectRandomDistichCount().executeAsOne()
            if (count == 0L) {
                Result.Error(DataError.Local.UNKNOWN)
            } else {
                val offset = (seed and Long.MAX_VALUE) % count
                val distich =
                    verseQueries
                        .selectRandomDistichAtOffset(offset)
                        .executeAsOneOrNull()
                        ?.toRandomDistich()
                if (distich == null) {
                    Result.Error(DataError.Local.UNKNOWN)
                } else {
                    Result.Success(distich)
                }
            }
        } catch (_: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
}
