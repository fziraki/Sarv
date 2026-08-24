package abkabk.azbarkon.features.tasvirNegar.util

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberTasvirNegarStoragePermission(onResult: (Boolean) -> Unit): () -> Unit {
    val currentOnResult by rememberUpdatedState(onResult)
    val context = LocalContext.current

    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            currentOnResult(granted)
        }

    return remember(launcher, context) {
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                currentOnResult(true)
            } else if (
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                currentOnResult(true)
            } else {
                launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }
}
