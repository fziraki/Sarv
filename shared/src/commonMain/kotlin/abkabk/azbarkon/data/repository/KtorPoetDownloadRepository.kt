package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.EmptyResult
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.local.PoetDbDriverFactory
import abkabk.azbarkon.core.local.PoetDbFileStorage
import abkabk.azbarkon.core.util.Constants
import abkabk.azbarkon.data.local.mergePoetDatabase
import abkabk.azbarkon.domain.repository.PoetDownloadError
import abkabk.azbarkon.domain.repository.PoetDownloadRepository
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import com.azbarkon.db.AzbarKonDatabase
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class KtorPoetDownloadRepository(
    private val httpClient: HttpClient,
    private val storage: PoetDbFileStorage,
    private val poetDbDriverFactory: PoetDbDriverFactory,
    private val mainDatabase: AzbarKonDatabase,
    private val mainDriver: app.cash.sqldelight.db.SqlDriver,
) : PoetDownloadRepository {

    @Suppress("TooGenericExceptionCaught", "MagicNumber")
    override suspend fun downloadPoet(poetId: Int): EmptyResult<PoetDownloadError> {
        val fileName = "poet_$poetId.s3db"
        if (isAlreadyDownloaded(poetId)) {
            Napier.d(message = "poet $poetId already downloaded, skipping", tag = "PoetDownload")
            return Result.Success(Unit)
        }

        val url = "${Constants.POET_DB_RELEASE_URL}$fileName"
        Napier.d(message = "downloading $fileName from $url", tag = "PoetDownload")
        val response =
            try {
                httpClient.get(url)
            } catch (e: Exception) {
                Napier.e(message = "GET $url threw: ${e.message}", throwable = e, tag = "PoetDownload")
                return Result.Error(PoetDownloadError.Network)
            }
        val status = response.status.value
        if (!response.status.isSuccess()) {
            Napier.e(
                message = "GET $url status=$status body=${response.bodyAsText().take(200)}",
                tag = "PoetDownload",
            )
            return Result.Error(PoetDownloadError.Network)
        }
        val bytes = response.body<ByteArray>()
        Napier.d(message = "downloaded $fileName status=$status bytes=${bytes.size}", tag = "PoetDownload")

        storage.writeBytes(fileName, bytes)
        return try {
            withContext(Dispatchers.IO) {
                val path = "${storage.downloadDir()}/$fileName"
                Napier.d(message = "opening $path", tag = "PoetDownload")
                val source = poetDbDriverFactory.open(path)
                try {
                    logSourceInfo(source)
                    Napier.d(message = "merging poet $poetId into main db", tag = "PoetDownload")
                    mergePoetDatabase(mainDatabase, mainDriver, source, poetId = poetId.toLong())
                    Napier.d(message = "merge succeeded for poet $poetId", tag = "PoetDownload")
                } finally {
                    source.close()
                }
            }
            storage.delete(fileName)
            Napier.d(message = "cleaned up $fileName", tag = "PoetDownload")
            Result.Success(Unit)
        } catch (e: Exception) {
            Napier.e(message = "merge failed for poet $poetId: ${e.message}", throwable = e, tag = "PoetDownload")
            storage.delete(fileName)
            Result.Error(PoetDownloadError.MergeFailed)
        }
    }

    private fun logSourceInfo(source: SqlDriver) {
        val version =
            source.executeQuery<Long>(
                identifier = null,
                sql = "PRAGMA user_version",
                mapper = { cursor ->
                    if (cursor.next().value) {
                        QueryResult.Value(cursor.getLong(0) ?: 0L)
                    } else {
                        QueryResult.Value(0L)
                    }
                },
                parameters = 0,
            ).value
        val tables =
            source.executeQuery<List<String?>>(
                identifier = null,
                sql = "SELECT name FROM sqlite_master WHERE type='table' AND name IN ('poet_meta','verse_fts4','poet')",
                mapper = { cursor ->
                    QueryResult.Value(
                        buildList {
                            while (cursor.next().value) add(cursor.getString(0))
                        },
                    )
                },
                parameters = 0,
            ).value
        Napier.d(message = "source user_version=$version tables=$tables", tag = "PoetDownload")
    }

    private fun isAlreadyDownloaded(poetId: Int): Boolean =
        mainDatabase.catQueries.selectAllByPoetId(poet_id = poetId.toLong()).executeAsList().isNotEmpty()
}
