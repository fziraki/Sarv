package abkabk.azbarkon.features.tasvirNegar.util

import abkabk.azbarkon.core.platform.toByteArray
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL

@Composable
actual fun LocalGalleryImage(
    uri: String,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    var imageBitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        imageBitmap =
            withContext(Dispatchers.Default) {
                runCatching {
                    val data = NSData.dataWithContentsOfURL(NSURL.fileURLWithPath(uri)) ?: return@withContext null
                    data.toByteArray().decodeToImageBitmap()
                }.getOrNull()
            }
    }

    Box(modifier = modifier.background(Color.Transparent)) {
        imageBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
        }
    }
}
