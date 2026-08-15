package abkabk.azbarkon.features.profile.util

import androidx.compose.runtime.Composable

@Composable
expect fun rememberBackupImportLauncher(onResult: (String?) -> Unit): () -> Unit
