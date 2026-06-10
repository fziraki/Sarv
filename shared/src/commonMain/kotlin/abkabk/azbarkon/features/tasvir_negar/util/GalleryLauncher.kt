package abkabk.azbarkon.features.tasvir_negar.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberTasvirNegarGalleryLauncher(onResult: (String?) -> Unit): () -> Unit
