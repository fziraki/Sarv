package abkabk.azbarkon.features.tasvir_negar.util

import androidx.compose.runtime.Composable

@Composable
actual fun rememberTasvirNegarGalleryLauncher(onResult: (String?) -> Unit): () -> Unit =
    {
        onResult(null)
    }
