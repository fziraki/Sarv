package abkabk.azbarkon.features.tasvirNegar.util

import abkabk.azbarkon.features.tasvirNegar.model.TasvirNegarCatalog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.clear_cancel
import azbarkoncmp.shared.generated.resources.tasvir_pick_color
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun TasvirCustomColorPicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    onColorSelect: (Color) -> Unit,
) {
    if (!visible) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.tasvir_pick_color)) },
        text = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TasvirNegarCatalog.colorOptions
                    .filter { it.color != null }
                    .forEach { option ->
                        Box(
                            modifier =
                                Modifier
                                    .size(36.dp)
                                    .background(option.color!!, RoundedCornerShape(8.dp))
                                    .clickable { onColorSelect(option.color) },
                        )
                    }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.clear_cancel))
            }
        },
    )
}
