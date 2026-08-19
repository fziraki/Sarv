package abkabk.azbarkon.data.repository

import abkabk.azbarkon.core.domain.result.EmptyResult
import abkabk.azbarkon.core.domain.result.Result
import abkabk.azbarkon.core.local.PoetDbDriverFactory
import abkabk.azbarkon.core.local.PoetDbFileStorage
import abkabk.azbarkon.core.util.Constants
import abkabk.azbarkon.data.local.mergePoetDatabase
import abkabk.azbarkon.domain.repository.PoetDownloadError
import abkabk.azbarkon.domain.repository.PoetDownloadRepository
import com.azbarkon.db.AzbarKonDatabase
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class KtorPoetDownloadRepository(
    private val httpClient: HttpClient,
    private val storage: PoetDbFileStorage,
    private val poetDbDriverFactory: PoetDbDriverFactory,
    private val mainDatabase: AzbarKonDatabase,
    private val mainDriver: app.cash.sqldelight.db.SqlDriver,
) : PoetDownloadRepository {

    @Suppress("TooGenericExceptionCaught")
    override suspend fun downloadPoet(poetId: Int): EmptyResult<PoetDownloadError> {
        val fileName = "poet_$poetId.s3db"
        if (isAlreadyDownloaded(poetId)) {
            Napier.d(message = "poet $poetId already downloaded, skipping", tag = "PoetDownload")
            return Result.Success(Unit)
        }

        val url = "${Constants.POET_DB_RELEASE_URL}$fileName"
        Napier.d(message = "downloading $fileName from $url", tag = "PoetDownload")
        val bytes =
            try {
                httpClient.get(url).body<ByteArray>()
            } catch (e: Exception) {
                Napier.e(message = "GET $url failed: ${e.message}", throwable = e, tag = "PoetDownload")
                return Result.Error(PoetDownloadError.Network)
            }
        Napier.d(message = "downloaded ${bytes.size} bytes", tag = "PoetDownload")

        storage.writeBytes(fileName, bytes)
        return try {
            withContext(Dispatchers.IO) {
                val path = "${storage.downloadDir()}/$fileName"
                Napier.d(message = "opening $path", tag = "PoetDownload")
                val source = poetDbDriverFactory.open(path)
                try {
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

    private fun isAlreadyDownloaded(poetId: Int): Boolean =
        mainDatabase.catQueries.selectAllByPoetId(poet_id = poetId.toLong()).executeAsList().isNotEmpty()
}
