package abkabk.azbarkon.features.tasvirNegar.components

import abkabk.azbarkon.features.tasvirNegar.model.EditorFontPreset
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import azbarkoncmp.shared.generated.resources.B_Yekan
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.Shekasteh
import azbarkoncmp.shared.generated.resources.Tanha_FD
import org.jetbrains.compose.resources.Font

@Composable
fun editorFontFamily(preset: EditorFontPreset): FontFamily =
    when (preset) {
        EditorFontPreset.Shekasteh ->
            FontFamily(Font(Res.font.Shekasteh, FontWeight.Normal))
        EditorFontPreset.Yekan ->
            FontFamily(Font(Res.font.B_Yekan, FontWeight.Normal))
        EditorFontPreset.Tanha ->
            FontFamily(Font(Res.font.Tanha_FD, FontWeight.Normal))
    }
