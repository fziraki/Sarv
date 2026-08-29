package abkabk.azbarkon.core.platform

import android.content.ClipData
import android.content.ClipboardManager as AndroidClipboardManager
import android.content.Context

actual class ClipboardManager(
    context: Context,
) {
    private val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager

    actual fun copyToClipboard(text: String) {
        clipboard.setPrimaryClip(ClipData.newPlainText("poem", text))
    }

    actual fun readClipboardText(): String? =
        clipboard.primaryClip
            ?.getItemAt(0)
            ?.text
            ?.toString()
            ?.takeIf { it.isNotBlank() }
}
