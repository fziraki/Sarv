package abkabk.azbarkon.features.tasvir_negar.util

import abkabk.azbarkon.features.tasvir_negar.model.TasvirNegarColors
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

@Composable
actual fun LocalGalleryImage(
    uri: String,
    modifier: Modifier,
    contentScale: ContentScale,
) {
    Box(modifier = modifier.background(TasvirNegarColors.canvasDefault))
}
