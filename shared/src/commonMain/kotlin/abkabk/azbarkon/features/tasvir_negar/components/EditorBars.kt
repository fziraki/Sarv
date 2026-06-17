package abkabk.azbarkon.features.tasvir_negar.components

import abkabk.azbarkon.core.designsystem.secondary
import abkabk.azbarkon.core.designsystem.secondaryFixed
import abkabk.azbarkon.features.tasvir_negar.TasvirNegarAction
import abkabk.azbarkon.features.tasvir_negar.model.TasvirNegarColors
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import azbarkoncmp.shared.generated.resources.Res
import azbarkoncmp.shared.generated.resources.arrow_back_right
import azbarkoncmp.shared.generated.resources.cd_back
import azbarkoncmp.shared.generated.resources.download
import azbarkoncmp.shared.generated.resources.ic_color
import azbarkoncmp.shared.generated.resources.ic_delete
import azbarkoncmp.shared.generated.resources.ic_edit
import azbarkoncmp.shared.generated.resources.ic_grid
import azbarkoncmp.shared.generated.resources.ic_sticker
import azbarkoncmp.shared.generated.resources.ic_text_format
import azbarkoncmp.shared.generated.resources.ic_texture
import azbarkoncmp.shared.generated.resources.ic_wallpaper
import azbarkoncmp.shared.generated.resources.reset_image
import azbarkoncmp.shared.generated.resources.share
import azbarkoncmp.shared.generated.resources.tasvir_eraser
import azbarkoncmp.shared.generated.resources.tasvir_negar_save
import azbarkoncmp.shared.generated.resources.text_fields
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditorHeader(
    onResetClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(TasvirNegarColors.brown),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(Res.drawable.arrow_back_right),
                contentDescription = stringResource(Res.string.cd_back),
                tint = Color.Unspecified,
            )
        }

        Box(modifier = Modifier.weight(1f))

        IconButton(onClick = onResetClick) {
            Icon(
                painter = painterResource(Res.drawable.reset_image),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
fun EditorFooter(
    onEraserClick: () -> Unit,
    onDownloadClick: () -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
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

        IconButton(onClick = onDownloadClick) {
            Icon(
                painter = painterResource(Res.drawable.download),
                contentDescription = stringResource(Res.string.tasvir_negar_save),
                tint = Color.Unspecified,
            )
        }

        IconButton(onClick = onEraserClick) {
            Icon(
                painter = painterResource(Res.drawable.ic_delete),
                contentDescription = stringResource(Res.string.tasvir_eraser),
                tint = Color.Unspecified,
            )
        }

        IconButton(onClick = onEditClick) {
            Icon(
                painter = painterResource(Res.drawable.ic_edit),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }

        IconButton(onClick = onShareClick) {
            Icon(
                painter = painterResource(Res.drawable.share),
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
        ToolbarIcon(Res.drawable.ic_text_format, iconTint) { onAction(TasvirNegarAction.OnShowFontOptions) }
        ToolbarIcon(Res.drawable.text_fields, iconTint) { onAction(TasvirNegarAction.OnEnterText) }
        ToolbarIcon(Res.drawable.ic_sticker, iconTint) { onAction(TasvirNegarAction.OnShowShapeOptions) }
        ToolbarIcon(Res.drawable.ic_color, iconTint) { onAction(TasvirNegarAction.OnShowColorOptions) }
        ToolbarIcon(Res.drawable.ic_texture, iconTint) { onAction(TasvirNegarAction.OnLayerSelect(null)) }
        ToolbarIcon(Res.drawable.ic_grid, iconTint) { onAction(TasvirNegarAction.OnToggleGrid) }
        ToolbarIcon(Res.drawable.ic_wallpaper, iconTint) { onAction(TasvirNegarAction.OnGalleryClick) }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalSizeSlider(
    progress: Float,
    onProgressChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val valueRange = 12f..32f
    val sliderLength = 200.dp
    val touchWidth = 32.dp
    val thumbSize = 16.dp
    val thumbBorderWidth = 2.dp
    val trackHeight = 4.dp
    val sliderValue = progress.coerceIn(valueRange.start, valueRange.endInclusive)
    val samim = samimFontFamily()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = sliderValue.toInt().toString(),
            style =
                androidx.compose.ui.text.TextStyle(
                    fontFamily = samim,
                    fontSize = 12.sp,
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                ),
        )

        Box(
            modifier =
                Modifier
                    .width(touchWidth)
                    .height(sliderLength),
            contentAlignment = Alignment.Center,
        ) {
            // Keep LTR for the rotated horizontal Slider so vertical drag maps correctly in RTL screens.
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Slider(
                    value = sliderValue,
                    onValueChange = onProgressChange,
                    valueRange = valueRange,
                    modifier =
                        Modifier
                            .verticalSliderTransform()
                            .width(sliderLength)
                            .height(touchWidth),
                    colors =
                        SliderDefaults.colors(
                            thumbColor = Color.Transparent,
                            activeTrackColor = Color.Transparent,
                            inactiveTrackColor = Color.Transparent,
                        ),
                    thumb = {
                        Box(
                            modifier =
                                Modifier
                                    .size(thumbSize)
                                    .background(
                                        color = secondaryFixed,
                                        shape = CircleShape,
                                    ),
                        )
                    },
                    track = { sliderState ->
                        val trackProgress =
                            (sliderState.value - valueRange.start) /
                                (valueRange.endInclusive - valueRange.start).coerceAtLeast(0.001f)
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(trackHeight)
                                    .clip(RoundedCornerShape(percent = 50))
                                    .background(Color.Gray.copy(alpha = 0.25f)),
                        ) {
                            if (trackProgress > 0f) {
                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxHeight()
                                            .weight(trackProgress)
                                            .background(secondary),
                                )
                            }
                            if (trackProgress < 1f) {
                                Spacer(
                                    modifier =
                                        Modifier
                                            .fillMaxHeight()
                                            .weight(1f - trackProgress),
                                )
                            }
                        }
                    },
                )
            }
        }
    }
}

private fun Modifier.verticalSliderTransform(): Modifier =
    this
        .graphicsLayer {
            rotationZ = 270f
            transformOrigin = TransformOrigin(0f, 0f)
        }.layout { measurable, constraints ->
            val placeable =
                measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxHeight,
                    ),
                )
            layout(placeable.height, placeable.width) {
                placeable.place(-placeable.width, 0)
            }
        }

@Preview
@Composable
private fun VerticalSizeSliderPreview() {
    AzbarkonTheme {
        var progress by remember { mutableFloatStateOf(22f) }
        VerticalSizeSlider(
            progress = progress,
            onProgressChange = { progress = it },
        )
    }
}
