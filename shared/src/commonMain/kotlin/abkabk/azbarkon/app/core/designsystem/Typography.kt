package abkabk.azbarkon.app.core.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun azbarkonTypography(): Typography {
    val fontFamily = vazirmatnFontFamily()

    return Typography(
        headlineSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        ),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        ),

        bodySmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 20.sp
        ),

        labelSmall = TextStyle(
            fontFamily = fontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Thin
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Thin
        ),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Thin
        ),
    )
}