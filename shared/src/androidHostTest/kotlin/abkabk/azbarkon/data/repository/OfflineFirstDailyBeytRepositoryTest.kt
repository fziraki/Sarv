package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.DataError
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.util.currentLocalDateSeed
import abkabk.azbarkon.domain.datasource.DailyBeytLocalDataSource
import abkabk.azbarkon.domain.model.RandomDistich
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class OfflineFirstDailyBeytRepositoryTest {
    @Test
    fun `getTodayDistich uses local data source with stable seed`() =
        runTest {
            val fakeDataSource = FakeDailyBeytLocalDataSource()
            val repository = OfflineFirstDailyBeytRepository(fakeDataSource)

            val result = repository.getTodayDistich()

            assertThat(result).isInstanceOf(Result.Success::class)
            assertThat((result as Result.Success).data.poetName).isEqualTo("حافظ")
            assertThat(fakeDataSource.lastSeed).isEqualTo(currentLocalDateSeed())
        }

    private class FakeDailyBeytLocalDataSource : DailyBeytLocalDataSource {
        var lastSeed: Long? = null

        override suspend fun getRandomDistich(seed: Long): Result<RandomDistich, DataError.Local> {
            lastSeed = seed
            return Result.Success(
                RandomDistich(
                    poemId = 10,
                    vorder = 2,
                    rightText = "الا یا ایها الساقی",
                    leftText = "بدو تا جام جم بده",
                    poetName = "حافظ",
                ),
            )
        }
    }
}
