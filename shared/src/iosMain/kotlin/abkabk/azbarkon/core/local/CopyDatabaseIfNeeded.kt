package abkabk.azbarkon.core.local

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSBundle
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

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

@OptIn(ExperimentalForeignApi::class)
internal fun copyDatabaseIfNeeded() {
    val fileManager = NSFileManager.defaultManager
    val dbPath = bundledDatabasePath() ?: return

    if (!fileManager.fileExistsAtPath(dbPath)) {
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
}
