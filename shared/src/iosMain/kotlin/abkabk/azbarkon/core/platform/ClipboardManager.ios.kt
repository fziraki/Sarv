@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package abkabk.azbarkon.core.platform

import platform.UIKit.UIPasteboard

actual class ClipboardManager {
    actual fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    actual fun readClipboardText(): String? =
        UIPasteboard.generalPasteboard.string?.takeIf { it.isNotBlank() }
}
