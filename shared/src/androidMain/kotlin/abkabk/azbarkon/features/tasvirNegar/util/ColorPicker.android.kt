package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun TasvirCustomColorPicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    onColorSelect: (Color) -> Unit,
) {
    if (visible) {
        HsvColorPickerContent(
            onDismiss = onDismiss,
            onColorSelect = onColorSelect,
        )
    }
}
