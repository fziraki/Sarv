package abkabk.azbarkon.ui.components

import abkabk.azbarkon.ui.theme.LightColorScheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

private const val TRACK_MIN_DIVISOR = 0.001f
private const val VERTICAL_ROTATION_DEGREES = 270f
private val THUMB_SIZE: Dp = 16.dp
private val TRACK_HEIGHT: Dp = 4.dp
private val TOUCH_WIDTH: Dp = 32.dp
private val SLIDER_LENGTH: Dp = 200.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SarvSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    onValueChangeFinished: (() -> Unit)? = null,
    vertical: Boolean = false,
    activeTrackColor: Color = MaterialTheme.colorScheme.primary,
    inactiveTrackColor: Color = LightColorScheme.outlineVariant,
    thumbColor: Color = MaterialTheme.colorScheme.primary,
) {
    val slider: @Composable (Modifier) -> Unit = { sliderModifier ->
        Slider(
            value = value.coerceIn(valueRange),
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = valueRange,
            modifier = sliderModifier,
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
                            .size(THUMB_SIZE)
                            .background(thumbColor, CircleShape),
                )
            },
            track = { sliderState ->
                val trackProgress =
                    (sliderState.value - sliderState.valueRange.start) /
                        (sliderState.valueRange.endInclusive - sliderState.valueRange.start)
                            .coerceAtLeast(TRACK_MIN_DIVISOR)
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(TRACK_HEIGHT)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(inactiveTrackColor),
                ) {
                    if (trackProgress > 0f) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxHeight()
                                    .weight(trackProgress)
                                    .background(activeTrackColor),
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

    if (vertical) {
        // Keep LTR for the rotated horizontal Slider so vertical drag maps correctly in RTL screens.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(
                modifier = modifier.width(TOUCH_WIDTH).height(SLIDER_LENGTH),
                contentAlignment = Alignment.Center,
            ) {
                slider(
                    Modifier
                        .verticalSliderTransform()
                        .width(SLIDER_LENGTH)
                        .height(TOUCH_WIDTH),
                )
            }
        }
    } else {
        slider(modifier)
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
