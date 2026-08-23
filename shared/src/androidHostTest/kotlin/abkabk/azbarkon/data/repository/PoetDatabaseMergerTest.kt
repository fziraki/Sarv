package abkabk.azbarkon.data.repository

import abkabk.azbarkon.data.local.mergePoetDatabase
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isGreaterThan
import com.azbarkon.db.AzbarKonDatabase
import java.io.File
import java.nio.file.Files
import org.junit.jupiter.api.Test

class PoetDatabaseMergerTest {

    @Test
    fun `merge adds Sanaei to the default database with searchable fts`() {
        val targetFile = tempCopy(resolveFile("ganjoor.s3db"))
        val sourceFile = resolveFile("poet_10.s3db")

        val target = JdbcSqliteDriver("jdbc:sqlite:${targetFile.absolutePath}")
        val source = JdbcSqliteDriver("jdbc:sqlite:${sourceFile.absolutePath}")

        val sourceVerses = source.countLong("SELECT COUNT(*) FROM verse")

        val database = AzbarKonDatabase(target)
        mergePoetDatabase(database, target, source, poetId = 10)

        assertThat(database.poetQueries.selectByIdWithCatUrl(10).executeAsOne().is_downloaded).isEqualTo(true)

        val mergedVerses =
            target.countLong(
                "SELECT COUNT(*) FROM verse WHERE poem_id IN (SELECT id FROM poem WHERE cat_id IN (SELECT id FROM cat WHERE poet_id = 10))",
            )
        assertThat(mergedVerses).isEqualTo(sourceVerses)

        val mergedFts =
            target.countLong(
                "SELECT COUNT(*) FROM verse_fts4 WHERE docid IN (SELECT rowid FROM verse WHERE poem_id IN (SELECT id FROM poem WHERE cat_id IN (SELECT id FROM cat WHERE poet_id = 10)))",
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
        val targetFile = tempCopy(resolveFile("ganjoor.s3db"))
        val sourceFile = resolveFile("poet_10.s3db")

        val target = JdbcSqliteDriver("jdbc:sqlite:${targetFile.absolutePath}")
        val database = AzbarKonDatabase(target)

        repeat(2) {
            val source = JdbcSqliteDriver("jdbc:sqlite:${sourceFile.absolutePath}")
            mergePoetDatabase(database, target, source, poetId = 10)
            source.close()
        }

        val catCount = target.countLong("SELECT COUNT(*) FROM cat WHERE poet_id = 10")
        val expectedCatCount =
            JdbcSqliteDriver("jdbc:sqlite:${sourceFile.absolutePath}").use { source ->
                source.countLong("SELECT COUNT(*) FROM cat WHERE poet_id = 10")
            }
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

    private fun tempCopy(source: File): File {
        val target = Files.createTempFile("merge_test", ".s3db").toFile()
        source.copyTo(target, overwrite = true)
        return target
    }

    private fun resolveFile(name: String): File {
        val candidates =
            listOf(
                File("tools/out/$name"),
                File("../tools/out/$name"),
                File("../../tools/out/$name"),
            )
        return candidates.firstOrNull { it.exists() } ?: error("tools/out/$name not found")
    }
}
