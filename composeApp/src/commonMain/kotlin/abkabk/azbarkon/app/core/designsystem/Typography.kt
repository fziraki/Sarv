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
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 14.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fontFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    )
}