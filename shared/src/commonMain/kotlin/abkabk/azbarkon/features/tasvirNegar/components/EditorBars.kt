package abkabk.azbarkon.features.tasvirNegar.components

import abkabk.azbarkon.core.designsystem.secondary
import abkabk.azbarkon.core.designsystem.surfaceVariant
import abkabk.azbarkon.features.tasvirNegar.TasvirNegarAction
import abkabk.azbarkon.ui.theme.AzbarkonTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
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
import azbarkoncmp.shared.generated.resources.tasvir_color
import azbarkoncmp.shared.generated.resources.tasvir_edit
import azbarkoncmp.shared.generated.resources.tasvir_eraser
import azbarkoncmp.shared.generated.resources.tasvir_font
import azbarkoncmp.shared.generated.resources.tasvir_gallery
import azbarkoncmp.shared.generated.resources.tasvir_grid
import azbarkoncmp.shared.generated.resources.tasvir_negar_save
import azbarkoncmp.shared.generated.resources.tasvir_share
import azbarkoncmp.shared.generated.resources.tasvir_sticker
import azbarkoncmp.shared.generated.resources.tasvir_text
import azbarkoncmp.shared.generated.resources.tasvir_texture
import azbarkoncmp.shared.generated.resources.text_fields
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val MIN_TEXT_SIZE = 12f
private const val MAX_TEXT_SIZE = 32f
private const val TRACK_MIN_DIVISOR = 0.001f
private const val VERTICAL_ROTATION_DEGREES = 270f
private const val PREVIEW_PROGRESS = 22f

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
                .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(Res.drawable.arrow_back_right),
                contentDescription = stringResource(Res.string.cd_back),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Box(modifier = Modifier.weight(1f))

        IconButton(onClick = onResetClick) {
            Icon(
                painter = painterResource(Res.drawable.reset_image),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
private fun LabeledIconButton(
    drawable: org.jetbrains.compose.resources.DrawableResource,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            modifier = Modifier.clickable{
                onClick()
            },
            painter = painterResource(drawable),
            contentDescription = label,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp,
            fontFamily = samimFontFamily(),
        )
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
                .height(72.dp)
                .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        LabeledIconButton(
            drawable = Res.drawable.download,
            label = stringResource(Res.string.tasvir_negar_save),
            onClick = onDownloadClick,
        )
        LabeledIconButton(
            drawable = Res.drawable.ic_delete,
            label = stringResource(Res.string.tasvir_eraser),
            onClick = onEraserClick,
        )
        LabeledIconButton(
            drawable = Res.drawable.ic_edit,
            label = stringResource(Res.string.tasvir_edit),
            onClick = onEditClick,
        )
        LabeledIconButton(
            drawable = Res.drawable.share,
            label = stringResource(Res.string.tasvir_share),
            onClick = onShareClick,
        )
    }
}

@Composable
fun EditToolbar(
    onAction: (TasvirNegarAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val tint = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(68.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        ToolbarIcon(Res.drawable.ic_text_format, tint, stringResource(Res.string.tasvir_font)) {
            onAction(TasvirNegarAction.OnShowFontOptions)
        }
        ToolbarIcon(Res.drawable.text_fields, tint, stringResource(Res.string.tasvir_text)) {
            onAction(TasvirNegarAction.OnEnterText)
        }
        ToolbarIcon(Res.drawable.ic_sticker, tint, stringResource(Res.string.tasvir_sticker)) {
            onAction(TasvirNegarAction.OnShowShapeOptions)
        }
        ToolbarIcon(Res.drawable.ic_color, tint, stringResource(Res.string.tasvir_color)) {
            onAction(TasvirNegarAction.OnShowColorOptions)
        }
        ToolbarIcon(Res.drawable.ic_texture, tint, stringResource(Res.string.tasvir_texture)) {
            onAction(TasvirNegarAction.OnLayerSelect(null))
        }
        ToolbarIcon(Res.drawable.ic_grid, tint, stringResource(Res.string.tasvir_grid)) {
            onAction(TasvirNegarAction.OnToggleGrid)
        }
        ToolbarIcon(Res.drawable.ic_wallpaper, tint, stringResource(Res.string.tasvir_gallery)) {
            onAction(TasvirNegarAction.OnGalleryClick)
        }
    }
}

@Composable
private fun ToolbarIcon(
    drawable: org.jetbrains.compose.resources.DrawableResource,
    tint: Color,
    label: String,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            modifier = Modifier.clickable{
                onClick()
            },
            painter = painterResource(drawable),
            contentDescription = label,
            tint = tint,
        )
        Text(
            text = label,
            color = tint,
            fontSize = 10.sp,
            fontFamily = samimFontFamily(),
            modifier = Modifier.padding(top = 2.dp),
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
    val valueRange = MIN_TEXT_SIZE..MAX_TEXT_SIZE
    val sliderLength = 200.dp
    val touchWidth = 32.dp
    val thumbSize = 16.dp
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
                                        color = surfaceVariant,
                                        shape = CircleShape,
                                    ),
                        )
                    },
                    track = { sliderState ->
                        val trackProgress =
                            (sliderState.value - valueRange.start) /
                                (valueRange.endInclusive - valueRange.start).coerceAtLeast(TRACK_MIN_DIVISOR)
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
            rotationZ = VERTICAL_ROTATION_DEGREES
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
        var progress by remember { mutableFloatStateOf(PREVIEW_PROGRESS) }
        VerticalSizeSlider(
            progress = progress,
            onProgressChange = { progress = it },
        )
    }
}
