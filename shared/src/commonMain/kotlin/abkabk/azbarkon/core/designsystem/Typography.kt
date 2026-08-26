package abkabk.azbarkon.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun sarvTypography(fontSizeScale: Float = 1f): Typography {
    val fontFamily = vazirmatnFontFamily()

    return Typography(
        headlineSmall =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (12 * fontSizeScale).sp,
                fontWeight = FontWeight.Bold,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (14 * fontSizeScale).sp,
                fontWeight = FontWeight.Bold,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (16 * fontSizeScale).sp,
                fontWeight = FontWeight.Bold,
            ),
        bodySmall =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (12 * fontSizeScale).sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (14 * fontSizeScale).sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (16 * fontSizeScale).sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (12 * fontSizeScale).sp,
                fontWeight = FontWeight.Thin,
            ),
        labelMedium =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (14 * fontSizeScale).sp,
                fontWeight = FontWeight.Thin,
            ),
        labelLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (16 * fontSizeScale).sp,
                fontWeight = FontWeight.Thin,
            ),
    )
}
