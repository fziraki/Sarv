package abkabk.azbarkon.core.platform

expect class ClipboardManager {
    fun copyToClipboard(text: String)
    fun readClipboardText(): String?
}
