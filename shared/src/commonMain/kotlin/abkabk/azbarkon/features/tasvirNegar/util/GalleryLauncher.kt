package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberTasvirNegarGalleryLauncher(onResult: (String?) -> Unit): () -> Unit
