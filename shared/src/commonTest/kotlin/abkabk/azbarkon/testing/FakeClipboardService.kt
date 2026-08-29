package abkabk.azbarkon.testing

import abkabk.azbarkon.domain.platform.ClipboardService

class FakeClipboardService : ClipboardService {
    var lastCopiedText: String? = null

    override fun copyToClipboard(text: String) {
        lastCopiedText = text
    }
}
