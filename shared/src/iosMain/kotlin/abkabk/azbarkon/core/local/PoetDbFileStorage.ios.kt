@file:OptIn(ExperimentalForeignApi::class)

package abkabk.azbarkon.core.local

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.writeToFile

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

private fun ByteArray.toNSData(): NSData =
    usePinned { pinned ->
        NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
    }
