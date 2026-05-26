package abkabk.azbarkon.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun NetworkImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    KamelImage(
        modifier = modifier,
        resource = asyncPainterResource(imageUrl),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}