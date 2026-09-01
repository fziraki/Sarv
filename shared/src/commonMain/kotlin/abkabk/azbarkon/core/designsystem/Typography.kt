package abkabk.azbarkon.core.designsystem

import abkabk.azbarkon.core.ui.LocalWindowSizeClass
import abkabk.azbarkon.core.ui.WindowWidthSizeClass
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun sarvTypography(
    fontSizeScale: Float = 1f,
): Typography {
    val fontFamily = vazirmatnFontFamily()
    val offset = when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> 0f
        WindowWidthSizeClass.Medium -> 8f
        WindowWidthSizeClass.Expanded -> 12f
    }

    fun sp(base: Int) = ((base + offset) * fontSizeScale).sp

    return Typography(
        headlineSmall = TextStyle(fontFamily = fontFamily, fontSize = sp(12), fontWeight = FontWeight.Bold),
        headlineMedium = TextStyle(fontFamily = fontFamily, fontSize = sp(14), fontWeight = FontWeight.Bold),
        headlineLarge = TextStyle(fontFamily = fontFamily, fontSize = sp(16), fontWeight = FontWeight.Bold),
        bodySmall = TextStyle(fontFamily = fontFamily, fontSize = sp(12)),
        bodyMedium = TextStyle(fontFamily = fontFamily, fontSize = sp(14)),
        bodyLarge = TextStyle(fontFamily = fontFamily, fontSize = sp(16)),
        labelSmall = TextStyle(fontFamily = fontFamily, fontSize = sp(12), fontWeight = FontWeight.Thin),
        labelMedium = TextStyle(fontFamily = fontFamily, fontSize = sp(14), fontWeight = FontWeight.Thin),
        labelLarge = TextStyle(fontFamily = fontFamily, fontSize = sp(16), fontWeight = FontWeight.Thin),
    )
}
