package abkabk.azbarkon.core.local

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.NSBundle

internal const val DATABASE_NAME = "ganjoor.s3db"

@OptIn(ExperimentalForeignApi::class)
internal fun copyDatabaseIfNeeded() {
    val fileManager = NSFileManager.defaultManager
    val documentDirectory =
        NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true,
        ).firstOrNull() as? String ?: return

    val dbPath = "$documentDirectory/$DATABASE_NAME"
    if (fileManager.fileExistsAtPath(dbPath)) return

    val bundlePath =
        NSBundle.mainBundle.pathForResource(
            "ganjoor",
            ofType = "s3db",
        ) ?: return

    fileManager.copyItemAtPath(
        bundlePath,
        toPath = dbPath,
        error = null,
    )
}
