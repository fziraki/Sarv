package abkabk.azbarkon.core.designsystem

import abkabk.azbarkon.core.ui.LocalWindowSizeClass
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.math.floor

private const val BASE_WIDTH_DP = 360

private const val HEADLINE_SMALL_BASE = 12f
private const val HEADLINE_MEDIUM_BASE = 14f
private const val HEADLINE_LARGE_BASE = 16f
private const val BODY_SMALL_BASE = 12f
private const val BODY_MEDIUM_BASE = 14f
private const val BODY_LARGE_BASE = 16f
private const val LABEL_SMALL_EMPHASIZED_BASE = 10f
private const val LABEL_SMALL_BASE = 12f
private const val LABEL_MEDIUM_BASE = 14f
private const val LABEL_LARGE_BASE = 16f

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun sarvTypography(
    fontSizeScale: Float = 1f,
): Typography {
    val fontFamily = vazirmatnFontFamily()
    val widthDp = LocalWindowSizeClass.current.widthDp
    val heightDp = LocalWindowSizeClass.current.heightDp

    val min = minOf(widthDp, heightDp)

    val scale = floorToHalf((min / BASE_WIDTH_DP).toFloat())
    fun sp(base: Float) = (base * scale * fontSizeScale).sp

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
