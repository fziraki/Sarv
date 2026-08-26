package abkabk.azbarkon.data.repository

import abkabk.azbarkon.data.local.mergePoetDatabase
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.sarv.db.SarvDatabase
import java.nio.file.Files
import org.junit.jupiter.api.Test

class PoetDatabaseMergerTest {

    @Test
    fun `merge adds poet with cats and searchable fts`() {
        val sourceFile = createTestDatabase(poetId = TEST_POET_ID, verseText = "حدیثی از دل بی‌خبر")
        val targetFile = createTestDatabase(poetId = TEST_POET_ID, includeCats = false, includeVerses = false)

        val target = JdbcSqliteDriver("jdbc:sqlite:${targetFile.absolutePath}")
        val source = JdbcSqliteDriver("jdbc:sqlite:${sourceFile.absolutePath}")

        val sourceVerses = source.countLong("SELECT COUNT(*) FROM verse")

        val database = SarvDatabase(target)
        mergePoetDatabase(database, target, source, poetId = TEST_POET_ID)

        assertThat(database.poetQueries.selectByIdWithCatUrl(TEST_POET_ID).executeAsOne().is_downloaded).isEqualTo(true)

        val mergedVerses =
            target.countLong(
                "SELECT COUNT(*) FROM verse WHERE poem_id IN (SELECT id FROM poem WHERE cat_id IN (SELECT id FROM cat WHERE poet_id = $TEST_POET_ID))",
            )
        assertThat(mergedVerses).isEqualTo(sourceVerses)

        val mergedFts =
            target.countLong(
                "SELECT COUNT(*) FROM verse_fts4 WHERE docid IN (SELECT rowid FROM verse WHERE poem_id IN (SELECT id FROM poem WHERE cat_id IN (SELECT id FROM cat WHERE poet_id = $TEST_POET_ID)))",
            )
        assertThat(mergedFts).isEqualTo(mergedVerses)

        val searchHit =
            target.countLong(
                "SELECT COUNT(*) FROM verse_fts4 WHERE verse_fts4 MATCH 'حدیثی'",
            )
        assertThat(searchHit).isGreaterThan(0)
    }

    @Test
    fun `merging twice does not duplicate rows`() {
        val sourceFile = createTestDatabase(poetId = TEST_POET_ID)
        val targetFile = createTestDatabase(poetId = TEST_POET_ID, includeCats = false, includeVerses = false)

        val target = JdbcSqliteDriver("jdbc:sqlite:${targetFile.absolutePath}")
        val database = SarvDatabase(target)

        repeat(2) {
            val source = JdbcSqliteDriver("jdbc:sqlite:${sourceFile.absolutePath}")
            mergePoetDatabase(database, target, source, poetId = TEST_POET_ID)
            source.close()
        }

        val catCount = target.countLong("SELECT COUNT(*) FROM cat WHERE poet_id = $TEST_POET_ID")
        val expectedCatCount = 1L
        assertThat(catCount).isEqualTo(expectedCatCount)
    }

    private fun SqlDriver.countLong(sql: String): Long {
        val result: QueryResult<Long> =
            executeQuery(
                identifier = null,
                sql = sql,
                mapper = { cursor ->
                    cursor.next().value
                    QueryResult.Value(cursor.getLong(0) ?: 0L)
                },
                parameters = 0,
            )
        return result.value
    }

    companion object {
        private const val TEST_POET_ID = 9999L

        private val SCHEMA_STATEMENTS = listOf(
            "CREATE TABLE poet (id INTEGER NOT NULL PRIMARY KEY, name TEXT NOT NULL, cat_id INTEGER NOT NULL, description TEXT NOT NULL)",
            "CREATE TABLE cat (id INTEGER NOT NULL PRIMARY KEY, poet_id INTEGER NOT NULL, text TEXT NOT NULL, parent_id INTEGER NOT NULL, url TEXT NOT NULL)",
            "CREATE TABLE poem (id INTEGER PRIMARY KEY, cat_id INTEGER, title NVARCHAR(255), url NVARCHAR(255))",
            "CREATE TABLE verse (poem_id INTEGER, vorder INTEGER, position INTEGER, text TEXT)",
            "CREATE TABLE poet_meta (id INTEGER NOT NULL PRIMARY KEY, slug TEXT NOT NULL)",
            "CREATE VIRTUAL TABLE verse_fts4 USING fts4(text, content='verse')",
        )

        private fun createTestDatabase(
            poetId: Long,
            includeCats: Boolean = true,
            includeVerses: Boolean = true,
            verseText: String = "حدیثی از دل بی‌خبر",
        ): java.io.File {
            val file = Files.createTempFile("merge_test_${poetId}", ".s3db").toFile()
            val driver = JdbcSqliteDriver("jdbc:sqlite:${file.absolutePath}")
            SCHEMA_STATEMENTS.forEach { driver.execute(null, it, 0) }

            val rootCatId = poetId * 100 + 1
            driver.execute(null, "INSERT INTO poet (id, name, cat_id, description) VALUES ($poetId, 'Test Poet', $rootCatId, '')", 0)
            driver.execute(null, "INSERT INTO poet_meta (id, slug) VALUES ($poetId, 'test-poet')", 0)

            if (includeCats) {
                driver.execute(null, "INSERT INTO cat (id, poet_id, text, parent_id, url) VALUES ($rootCatId, $poetId, 'Root', 0, 'https://example.com')", 0)
            }

            if (includeVerses) {
                val poemId = poetId * 100 + 10
                if (includeCats) {
                    driver.execute(null, "INSERT INTO poem (id, cat_id, title, url) VALUES ($poemId, $rootCatId, 'Test Poem', 'https://example.com/poem')", 0)
                }
                driver.execute(null, "INSERT INTO verse (poem_id, vorder, position, text) VALUES ($poemId, 1, 1, '$verseText')", 0)
                driver.execute(null, "INSERT INTO verse_fts4 (docid, text) VALUES (1, '$verseText')", 0)
            }

            driver.close()
            return file
        }
    }
}
