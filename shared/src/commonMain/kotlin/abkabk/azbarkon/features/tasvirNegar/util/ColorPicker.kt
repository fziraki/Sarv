package abkabk.azbarkon.features.tasvirNegar.util

import abkabk.azbarkon.ui.components.AzbarkonButton
import abkabk.azbarkon.ui.components.AzbarkonPrimaryButton
import abkabk.azbarkon.ui.components.AzbarkonSlider
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.clear_cancel
import azbarkoncmp.shared.generated.resources.tasvir_pick_color
import org.jetbrains.compose.resources.stringResource

@Composable
expect fun TasvirCustomColorPicker(
    visible: Boolean,
    onDismiss: () -> Unit,
    onColorSelect: (Color) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HsvColorPickerContent(
    onDismiss: () -> Unit,
    onColorSelect: (Color) -> Unit,
) {
    var hue by remember { mutableFloatStateOf(0f) }
    var saturation by remember { mutableFloatStateOf(1f) }
    var value by remember { mutableFloatStateOf(1f) }
    val selectedColor = remember(hue, saturation, value) { Color.hsv(hue, saturation, value) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState,
        sheetGesturesEnabled = false,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.tasvir_pick_color),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AzbarkonButton(
                    text = stringResource(Res.string.clear_cancel),
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                        ),
                )
                AzbarkonPrimaryButton(
                    text = stringResource(Res.string.tasvir_pick_color),
                    onClick = { onColorSelect(selectedColor) },
                    modifier = Modifier.weight(1f).height(48.dp),
                )
            }
        }
    }
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
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, modifier = Modifier.size(width = 80.dp, height = 24.dp))
        AzbarkonSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.weight(1f),
        )
    }
}

