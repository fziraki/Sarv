package abkabk.azbarkon.features.tasvirNegar.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberTasvirNegarStoragePermission(onResult: (Boolean) -> Unit): () -> Unit
