package abkabk.azbarkon.core.designsystem

import abkabk.azbarkon.core.ui.LocalWindowSizeClass
import abkabk.azbarkon.core.ui.WindowWidthSizeClass
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private const val HEADLINE_SMALL_BASE = 12
private const val HEADLINE_MEDIUM_BASE = 14
private const val HEADLINE_LARGE_BASE = 16
private const val BODY_SMALL_BASE = 12
private const val BODY_MEDIUM_BASE = 14
private const val BODY_LARGE_BASE = 16
private const val LABEL_SMALL_EMPHASIZED_BASE = 10
private const val LABEL_SMALL_BASE = 12
private const val LABEL_MEDIUM_BASE = 14
private const val LABEL_LARGE_BASE = 16

private const val COMPACT_OFFSET = 0f
private const val MEDIUM_OFFSET = 4f
private const val EXPANDED_OFFSET = 8f

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun sarvTypography(
    fontSizeScale: Float = 1f,
): Typography {
    val fontFamily = vazirmatnFontFamily()
    val offset = when (LocalWindowSizeClass.current.widthSizeClass) {
        WindowWidthSizeClass.Compact -> COMPACT_OFFSET
        WindowWidthSizeClass.Medium -> MEDIUM_OFFSET
        WindowWidthSizeClass.Expanded -> EXPANDED_OFFSET
    }

    fun sp(base: Int) = ((base + offset) * fontSizeScale).sp

    return Typography(
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(HEADLINE_SMALL_BASE),
            fontWeight = FontWeight.Bold,
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(HEADLINE_MEDIUM_BASE),
            fontWeight = FontWeight.Bold,
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(HEADLINE_LARGE_BASE),
            fontWeight = FontWeight.Bold,
        ),
        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(BODY_SMALL_BASE),
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(BODY_MEDIUM_BASE),
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(BODY_LARGE_BASE),
        ),
        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(LABEL_SMALL_BASE),
            fontWeight = FontWeight.Thin,
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(LABEL_MEDIUM_BASE),
            fontWeight = FontWeight.Thin,
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(LABEL_LARGE_BASE),
            fontWeight = FontWeight.Thin,
        ),
        labelSmallEmphasized = TextStyle(
            fontFamily = fontFamily,
            fontSize = sp(LABEL_SMALL_EMPHASIZED_BASE),
            fontWeight = FontWeight.Thin,
        )
    )
}
