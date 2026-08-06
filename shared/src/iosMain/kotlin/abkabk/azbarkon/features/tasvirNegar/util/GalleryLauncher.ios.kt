package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberTasvirNegarGalleryLauncher(onResult: (String?) -> Unit): () -> Unit =
    {
        onResult(null)
    }
