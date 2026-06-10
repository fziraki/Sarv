package abkabk.azbarkon.features.tasvir_negar.components

import abkabk.azbarkon.core.designsystem.secondary
import abkabk.azbarkon.features.tasvir_negar.TasvirNegarAction
import abkabk.azbarkon.features.tasvir_negar.model.TasvirNegarColors
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.arrow_back
import azbarkoncmp.shared.generated.resources.cd_back
import azbarkoncmp.shared.generated.resources.ic_color
import azbarkoncmp.shared.generated.resources.ic_delete
import azbarkoncmp.shared.generated.resources.ic_edit
import azbarkoncmp.shared.generated.resources.ic_grid
import azbarkoncmp.shared.generated.resources.ic_help
import azbarkoncmp.shared.generated.resources.ic_sticker
import azbarkoncmp.shared.generated.resources.ic_text_format
import azbarkoncmp.shared.generated.resources.ic_texture
import azbarkoncmp.shared.generated.resources.ic_wallpaper
import azbarkoncmp.shared.generated.resources.share
import azbarkoncmp.shared.generated.resources.tasvir_negar_save
import azbarkoncmp.shared.generated.resources.text_fields
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditorHeader(
    onSaveClick: () -> Unit,
    onHelpClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(TasvirNegarColors.brown)
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(Res.string.tasvir_negar_save),
            modifier = Modifier.clickable(onClick = onSaveClick),
            color = secondary,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
        )

        IconButton(
            onClick = onHelpClick,
            modifier = Modifier.padding(start = 8.dp).size(24.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_help),
                contentDescription = null,
                tint = secondary,
                modifier = Modifier.size(24.dp),
            )
        }

        Box(modifier = Modifier.weight(1f))

        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(Res.drawable.arrow_back),
                contentDescription = stringResource(Res.string.cd_back),
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
fun EditorFooter(
    onShareClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(TasvirNegarColors.brown),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        IconButton(onClick = onShareClick) {
            Icon(
                painter = painterResource(Res.drawable.share),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }

        IconButton(onClick = onEditClick) {
            Icon(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = null,
                tint = secondary,
            )
        }

        IconButton(onClick = onDeleteClick) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@Composable
fun EditToolbar(
    onAction: (TasvirNegarAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val iconTint = TasvirNegarColors.brown
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(TasvirNegarColors.brownAlpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ToolbarIcon(Res.drawable.ic_wallpaper, iconTint) { onAction(TasvirNegarAction.OnGalleryClick) }
        ToolbarIcon(Res.drawable.ic_grid, iconTint) { onAction(TasvirNegarAction.OnToggleGrid) }
        ToolbarIcon(Res.drawable.ic_texture, iconTint) { onAction(TasvirNegarAction.OnLayerSelect(null)) }
        ToolbarIcon(Res.drawable.ic_color, iconTint) { onAction(TasvirNegarAction.OnShowColorOptions) }
        ToolbarIcon(Res.drawable.ic_sticker, iconTint) { onAction(TasvirNegarAction.OnShowShapeOptions) }
        ToolbarIcon(Res.drawable.text_fields, iconTint) { onAction(TasvirNegarAction.OnEnterText) }
        ToolbarIcon(Res.drawable.ic_text_format, iconTint) { onAction(TasvirNegarAction.OnShowFontOptions) }
    }
}

@Composable
private fun ToolbarIcon(
    drawable: org.jetbrains.compose.resources.DrawableResource,
    tint: Color,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(drawable),
            contentDescription = null,
            tint = tint,
        )
    }
}


@Composable
fun FontSizeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 12f..32f
) {
    val thumbRadius = 8.dp
    val trackWidth = 2.dp

    Canvas(
        modifier = modifier
            .width(32.dp)
            .height(200.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        val ratio = 1f - (offset.y / size.height)
                        onValueChange(
                            (valueRange.start +
                                    ratio * (valueRange.endInclusive - valueRange.start))
                                .coerceIn(
                                    valueRange.start,
                                    valueRange.endInclusive
                                )
                        )
                    }
                ) { change, _ ->
                    val ratio = 1f - (change.position.y / size.height)
                    onValueChange(
                        (valueRange.start +
                                ratio * (valueRange.endInclusive - valueRange.start))
                            .coerceIn(
                                valueRange.start,
                                valueRange.endInclusive
                            )
                    )
                }
            }
    ) {
        val trackX = size.width / 2

        // Track
        drawLine(
            color = Color.Gray.copy(alpha = 0.4f),
            start = Offset(trackX, 0f),
            end = Offset(trackX, size.height),
            strokeWidth = trackWidth.toPx()
        )

        val progress =
            (value - valueRange.start) /
                    (valueRange.endInclusive - valueRange.start)

        val thumbY = size.height * (1f - progress)

        // Active track
        drawLine(
            color = Color.White,
            start = Offset(trackX, thumbY),
            end = Offset(trackX, size.height),
            strokeWidth = trackWidth.toPx()
        )

        // Bullet thumb
        drawCircle(
            color = Color.White,
            radius = thumbRadius.toPx(),
            center = Offset(trackX, thumbY)
        )
    }
}

@Composable
fun VerticalSizeSlider(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val samim = samimFontFamily()
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = progress.toString(),
            style =
                androidx.compose.ui.text.TextStyle(
                    fontFamily = samim,
                    fontSize = 12.sp,
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                ),
            modifier = Modifier,
        )

        FontSizeSlider(
            value = progress,
            onValueChange = onProgressChange,
            valueRange = 12f..32f
        )
    }
}

@Preview
@Composable
private fun VerticalSizeSliderPreview() {
    AzbarkonTheme {
        var progress by remember { mutableFloatStateOf(50f) }
        VerticalSizeSlider(
            progress = progress,
            onProgressChange = { progress = it },
        )
    }
}
