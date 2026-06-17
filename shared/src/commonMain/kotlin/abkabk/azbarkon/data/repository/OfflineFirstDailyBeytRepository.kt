package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.util.currentLocalDateSeed
import abkabk.azbarkon.domain.datasource.DailyBeytLocalDataSource
import abkabk.azbarkon.domain.model.RandomDistich
import abkabk.azbarkon.domain.repository.DailyBeytRepository

class OfflineFirstDailyBeytRepository(
    private val localDataSource: DailyBeytLocalDataSource,
) : DailyBeytRepository {
    override suspend fun getRandomDistich(
        seed: Long,
        poetId: Int,
    ): Result<RandomDistich, DataError.Local> =
        localDataSource.getRandomDistich(seed, poetId)

    override suspend fun getTodayDistich(): Result<RandomDistich, DataError.Local> =
        localDataSource.getRandomDistich(currentLocalDateSeed())
}
