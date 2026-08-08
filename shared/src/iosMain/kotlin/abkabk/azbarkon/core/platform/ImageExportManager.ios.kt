@file:OptIn(ExperimentalForeignApi::class)

package abkabk.azbarkon.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIImage
import platform.UIKit.UIImageWriteToSavedPhotosAlbum

actual class ImageExportManager {
    actual suspend fun saveToGallery(
        imageBytes: ByteArray,
        fileName: String,
    ): Boolean =
        runCatching {
            val image = UIImage(data = imageBytes.toNSData()) ?: return false
            // Fire-and-forget: the system prompts for photo library access on first use.
            UIImageWriteToSavedPhotosAlbum(image, null, null, null)
            true
        }.getOrDefault(false)
}
