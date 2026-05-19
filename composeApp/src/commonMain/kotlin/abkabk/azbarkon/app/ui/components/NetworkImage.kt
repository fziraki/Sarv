package abkabk.azbarkon.app.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun NetworkImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    KamelImage(
        modifier = modifier.clip(CircleShape),
        resource = asyncPainterResource(imageUrl),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}