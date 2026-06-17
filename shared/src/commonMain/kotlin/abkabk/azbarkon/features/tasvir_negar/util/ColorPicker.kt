package abkabk.azbarkon.features.tasvir_negar.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.clear_cancel
import azbarkoncmp.shared.generated.resources.tasvir_pick_color
import org.jetbrains.compose.resources.stringResource
import kotlin.math.max
import kotlin.math.min

@Composable
expect fun TasvirCustomColorPicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit,
)

@Composable
internal fun HsvColorPickerContent(
    onDismiss: () -> Unit,
    onColorSelected: (Color) -> Unit,
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var value by remember { mutableFloatStateOf(1f) }
    val selectedColor = remember(hue, saturation, value) { hsvToColor(hue, saturation, value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.tasvir_pick_color)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(selectedColor, RoundedCornerShape(8.dp)),
                )
                ColorSlider(label = "Hue", value = hue, valueRange = 0f..360f) { hue = it }
                ColorSlider(label = "Saturation", value = saturation) { saturation = it }
                ColorSlider(label = "Brightness", value = value) { value = it }
            }
        },
        confirmButton = {
            TextButton(onClick = { onColorSelected(selectedColor) }) {
                Text(stringResource(Res.string.tasvir_pick_color))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.clear_cancel))
            }
        },
    )
}

@Composable
private fun ColorSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChange: (Float) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = label, modifier = Modifier.size(width = 80.dp, height = 24.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun hsvToColor(
    hue: Float,
    saturation: Float,
    value: Float,
): Color {
    val h = ((hue % 360f) + 360f) % 360f / 60f
    val s = saturation.coerceIn(0f, 1f)
    val v = value.coerceIn(0f, 1f)
    val i = h.toInt()
    val f = h - i
    val p = v * (1 - s)
    val q = v * (1 - s * f)
    val t = v * (1 - s * (1 - f))
    val (r, g, b) =
        when (i % 6) {
            0 -> Triple(v, t, p)
            1 -> Triple(q, v, p)
            2 -> Triple(p, v, t)
            3 -> Triple(p, q, v)
            4 -> Triple(t, p, v)
            else -> Triple(v, p, q)
        }
    return Color(
        red = max(0f, min(1f, r)),
        green = max(0f, min(1f, g)),
        blue = max(0f, min(1f, b)),
    )
}
