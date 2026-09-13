package abkabk.azbarkon.core.local

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.runBlocking
import no.synth.kmpzip.zip.ZipInputStream
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.posix.FILE
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fwrite
import sarv.shared.generated.resources.Res

internal const val DATABASE_NAME = "ganjoor.s3db"

@OptIn(ExperimentalForeignApi::class)
internal fun documentsDirectory(): String? =
    NSSearchPathForDirectoriesInDomains(
        NSDocumentDirectory,
        NSUserDomainMask,
        true,
    ).firstOrNull() as? String

@OptIn(ExperimentalForeignApi::class)
internal fun bundledDatabasePath(): String? =
    documentsDirectory()?.let { "$it/$DATABASE_NAME" }

@OptIn(ExperimentalForeignApi::class)
internal fun hasBundledDatabase(): Boolean {
    val path = bundledDatabasePath() ?: return false
    return NSFileManager.defaultManager.fileExistsAtPath(path)
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal fun copyDatabaseIfNeeded() {
    val dbPath = bundledDatabasePath() ?: return

    if (!NSFileManager.defaultManager.fileExistsAtPath(dbPath)) {
        val zipBytes: ByteArray = runBlocking { Res.readBytes("files/$DATABASE_NAME.zip") }
        val extracted: ByteArray = ZipInputStream(zipBytes).use { zip ->
            zip.nextEntry ?: return@use ByteArray(0)
            zip.readBytes()
        }
        writeBytesToFile(extracted, dbPath)
    }
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
private fun writeBytesToFile(data: ByteArray, path: String) {
    data.usePinned { pinned ->
        val file: CPointer<FILE>? = fopen(path, "wb")
        if (file != null) {
            fwrite(pinned.addressOf(0), 1u, data.size.toULong(), file)
            fclose(file)
        }
    }
}
