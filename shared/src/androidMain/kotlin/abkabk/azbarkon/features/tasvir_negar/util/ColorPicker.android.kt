package abkabk.azbarkon.features.tasvir_negar.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
actual fun TasvirCustomColorPicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit,
) {
    if (visible) {
        HsvColorPickerContent(
            onDismiss = onDismiss,
            onColorSelected = onColorSelected,
        )
    }
}
