package abkabk.azbarkon.ui.components

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode

@Composable
fun shimmerEffectBrush(): ShaderBrush {
    val baseColor = MaterialTheme.colorScheme.surface
    val highlightColor = MaterialTheme.colorScheme.surfaceVariant
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer transition")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(durationMillis = 1_000)),
        label = "shimmer offset",
    )
    return remember(offset) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val shift = (size.width + size.height) * offset
                return LinearGradientShader(
                    colors = listOf(baseColor, highlightColor, baseColor),
                    from = Offset(-shift, -shift),
                    to = Offset(size.width - shift, size.height - shift),
                    tileMode = TileMode.Mirror,
                )
            }
        }
    }
}

@Composable
fun ShimmerPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(shimmerEffectBrush()),
    )
}
