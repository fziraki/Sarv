@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.platform

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ShareManager {
    actual fun shareText(
        text: String,
        title: String?,
    ) {
        val controller =
            UIActivityViewController(
                activityItems = listOf(text),
                applicationActivities = null,
            )
        val rootViewController =
            UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(controller, animated = true, completion = null)
    }
}
