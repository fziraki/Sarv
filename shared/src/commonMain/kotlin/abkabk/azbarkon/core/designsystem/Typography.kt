package abkabk.azbarkon.core.designsystem

import abkabk.azbarkon.core.ui.LocalWindowSizeClass
import androidx.compose.material3.Typography
import abkabk.azbarkon.core.ui.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun sarvTypography(
    fontSizeScale: Float = 1f,
    deviceScale: Float = deviceFontScale(),
): Typography {
    val fontFamily = vazirmatnFontFamily()
    val combined = fontSizeScale * deviceScale

    return Typography(
        headlineSmall =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (12 * combined).sp,
                fontWeight = FontWeight.Bold,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (14 * combined).sp,
                fontWeight = FontWeight.Bold,
            ),
        headlineLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (16 * combined).sp,
                fontWeight = FontWeight.Bold,
            ),
        bodySmall =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (12 * combined).sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (14 * combined).sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (16 * combined).sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (12 * combined).sp,
                fontWeight = FontWeight.Thin,
            ),
        labelMedium =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (14 * combined).sp,
                fontWeight = FontWeight.Thin,
            ),
        labelLarge =
            TextStyle(
                fontFamily = fontFamily,
                fontSize = (16 * combined).sp,
                fontWeight = FontWeight.Thin,
            ),
    )
}

@Composable
private fun deviceFontScale(): Float {
    val windowSizeClass = LocalWindowSizeClass.current
    return when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> 2.0f
        WindowWidthSizeClass.Medium -> 1.75f
        else -> 1.0f
    }
}
