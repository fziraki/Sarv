package abkabk.azbarkon.features.tasvirNegar.util

import abkabk.azbarkon.features.tasvirNegar.model.TextGravity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection

fun textLayoutDirectionFor(text: String): LayoutDirection {
    for (char in text) {
        if (char.isWhitespace()) continue
        val code = char.code
        if (isRtlCodePoint(code)) return LayoutDirection.Rtl
        if (char.isLetter() || char.isDigit()) return LayoutDirection.Ltr
    }
    return LayoutDirection.Rtl
}

fun textDirectionFor(text: String): TextDirection =
    when (textLayoutDirectionFor(text)) {
        LayoutDirection.Rtl -> TextDirection.Rtl
        LayoutDirection.Ltr -> TextDirection.Ltr
    }

/** Maps editor gravity to physical alignment, independent of content RTL/LTR. */
fun textAlignForGravity(gravity: TextGravity): TextAlign =
    when (gravity) {
        TextGravity.Start -> TextAlign.Left
        TextGravity.Center -> TextAlign.Center
        TextGravity.End -> TextAlign.Right
    }

private const val HEBREW_START = 0x0590
private const val HEBREW_END = 0x05FF
private const val ARABIC_START = 0x0600
private const val ARABIC_END = 0x06FF
private const val ARABIC_SUPPLEMENT_START = 0x0750
private const val ARABIC_SUPPLEMENT_END = 0x077F
private const val ARABIC_PRESENTATION_A_START = 0xFB50
private const val ARABIC_PRESENTATION_A_END = 0xFDFF
private const val ARABIC_PRESENTATION_B_START = 0xFE70
private const val ARABIC_PRESENTATION_B_END = 0xFEFF

private fun isRtlCodePoint(code: Int): Boolean =
    code in HEBREW_START..HEBREW_END ||
        code in ARABIC_START..ARABIC_END ||
        code in ARABIC_SUPPLEMENT_START..ARABIC_SUPPLEMENT_END ||
        code in ARABIC_PRESENTATION_A_START..ARABIC_PRESENTATION_A_END ||
        code in ARABIC_PRESENTATION_B_START..ARABIC_PRESENTATION_B_END
