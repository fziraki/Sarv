package abkabk.azbarkon.features.tasvir_negar.util

import abkabk.azbarkon.features.tasvir_negar.model.TextGravity
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

private fun isRtlCodePoint(code: Int): Boolean =
    code in 0x0590..0x05FF ||
        code in 0x0600..0x06FF ||
        code in 0x0750..0x077F ||
        code in 0xFB50..0xFDFF ||
        code in 0xFE70..0xFEFF
