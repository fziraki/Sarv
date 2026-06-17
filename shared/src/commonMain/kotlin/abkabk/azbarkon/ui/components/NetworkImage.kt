package abkabk.azbarkon.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.SubcomposeAsyncImage

@Composable
fun NetworkImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = null,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = {
            ShimmerPlaceholder(modifier = Modifier.fillMaxSize())
        },
        error = {
            ShimmerPlaceholder(modifier = Modifier.fillMaxSize())
        },
    )
}
