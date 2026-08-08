@file:OptIn(ExperimentalForeignApi::class)

package abkabk.azbarkon.core.platform

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectZero
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIImage

actual class ShareManager {
    actual fun shareText(
        text: String,
        title: String?,
    ) {
        presentShareSheet(listOf(text))
    }

    actual fun shareImage(
        imageBytes: ByteArray,
        title: String?,
    ) {
        val image = UIImage(data = imageBytes.toNSData()) ?: return
        presentShareSheet(listOf(image))
    }

    private fun presentShareSheet(items: List<*>) {
        val controller =
            UIActivityViewController(
                activityItems = items,
                applicationActivities = null,
            )
        val rootViewController =
            UIApplication.sharedApplication.keyWindow?.rootViewController
        // On iPad the sheet must anchor to a popover source, otherwise it crashes.
        controller.popoverPresentationController?.apply {
            sourceView = rootViewController?.view
            sourceRect = rootViewController?.view?.bounds ?: CGRectZero
        }
        rootViewController?.presentViewController(controller, animated = true, completion = null)
    }
}
