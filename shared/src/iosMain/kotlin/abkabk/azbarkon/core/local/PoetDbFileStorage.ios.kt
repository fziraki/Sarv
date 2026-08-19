@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.local

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile

@OptIn(ExperimentalForeignApi::class)
actual class PoetDbFileStorage {
    private val dir =
        NSSearchPathForDirectoriesInDomains(
            NSDocumentDirectory,
            NSUserDomainMask,
            true,
        ).firstOrNull() as? String ?: error("Documents directory unavailable")

    actual fun downloadDir(): String = dir

    actual fun writeBytes(fileName: String, bytes: ByteArray) {
        val data = bytes.toNSData()
        data.writeToFile("$dir/$fileName", atomically = true)
    }

    actual fun delete(fileName: String) {
        NSFileManager.defaultManager.removeItemAtPath("$dir/$fileName", error = null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData =
    NSData.create(bytes = this.refTo(0), length = size.toULong())
