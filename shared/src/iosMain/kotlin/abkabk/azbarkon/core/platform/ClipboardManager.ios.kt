package abkabk.azbarkon.core.platform

import platform.UIKit.UIPasteboard

actual class ClipboardManager {
    actual fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    actual fun readClipboardText(): String? =
        UIPasteboard.generalPasteboard.string?.takeIf { it.isNotBlank() }
}
