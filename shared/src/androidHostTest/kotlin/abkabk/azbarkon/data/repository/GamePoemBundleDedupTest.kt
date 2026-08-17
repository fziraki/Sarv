package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.data.local.SqlDelightGamesLocalDataSource
import abkabk.azbarkon.domain.model.games.GameConstants
import abkabk.azbarkon.domain.model.games.GameGenerationCache
import abkabk.azbarkon.domain.model.games.GameType
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.hasSize
import com.azbarkon.db.AzbarKonDatabase
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class GamePoemBundleDedupTest {
    @Test
    fun `buildPoemBundle tracks unique poem and poet ids across quiz slots`() =
        runBlocking {
            val dataSource = createDataSource()
            val cache = GameGenerationCache()

            repeat(3) { quizIndex ->
                when (val result = dataSource.buildPoemBundle(GameType.NEXT_VERSE, quizIndex, cache)) {
                    is Result.Success -> Unit
                    is Result.Error -> throw AssertionError("buildPoemBundle failed for index $quizIndex")
                }
            }

            assertThat(cache.poemBundles).hasSize(3)
            assertThat(cache.usedPoemIds).hasSize(3)
            assertThat(cache.usedPoetIds).hasSize(3)
            assertThat(cache.poemBundles.values.map { it.poemId }.toSet()).hasSize(3)
            assertThat(cache.poemBundles.values.map { it.poetId }.toSet()).hasSize(3)
        }

    @Test
    fun `generateQuizBatch builds ten unique poem bundles`() =
        runBlocking {
            val repository =
                OfflineFirstGamesRepository(
                    localDataSource = createDataSource(),
                )

            when (val result = repository.generateQuizBatch(GameType.NEXT_VERSE, seed = 42L, count = GameConstants.QUIZ_COUNT)) {
                is Result.Success -> assertThat(result.data).hasSize(GameConstants.QUIZ_COUNT)
                is Result.Error -> throw AssertionError("generateQuizBatch failed")
            }
        }

    private fun createDataSource(): SqlDelightGamesLocalDataSource {
        val dbFile = resolveBundledDatabaseFile()
        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        val database = AzbarKonDatabase(driver)
        return SqlDelightGamesLocalDataSource(
            verseQueries = database.verseQueries,
            poemQueries = database.poemQueries,
            poetQueries = database.poetQueries,
        )
    }

    private fun resolveBundledDatabaseFile(): File {
        val candidates =
            listOf(
                File("sqlite/ganjoor.s3db"),
                File("../shared/sqlite/ganjoor.s3db"),
                File("shared/sqlite/ganjoor.s3db"),
            )
        return candidates.firstOrNull { it.exists() }
            ?: error("Bundled ganjoor.s3db not found")
    }
}
