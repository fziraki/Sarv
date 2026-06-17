package abkabk.azbarkon.data.platform

import abkabk.azbarkon.core.platform.ClipboardManager
import abkabk.azbarkon.domain.platform.ClipboardService

class PlatformClipboardService(
    private val clipboardManager: ClipboardManager,
) : ClipboardService {
    override fun copyToClipboard(text: String) {
        clipboardManager.copyToClipboard(text)
    }
}
