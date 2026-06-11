package abkabk.azbarkon.features.tasvir_negar.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
expect fun LocalGalleryImage(
    uri: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
)

internal fun localImagePath(uri: String): String =
    when {
        uri.startsWith("file://") -> uri.removePrefix("file://")
        uri.startsWith("file:/") -> uri.removePrefix("file:")
        else -> uri
    }
