package abkabk.azbarkon.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.vazirmatn_bold
import azbarkoncmp.shared.generated.resources.vazirmatn_medium
import azbarkoncmp.shared.generated.resources.vazirmatn_regular
import org.jetbrains.compose.resources.Font

@Composable
fun vazirmatnFontFamily(): FontFamily {
    return FontFamily(
        Font(Res.font.vazirmatn_regular, FontWeight.Normal),
        Font(Res.font.vazirmatn_medium, FontWeight.Medium),
        Font(Res.font.vazirmatn_bold, FontWeight.Bold),
    )
}