package abkabk.azbarkon.features.tasvirNegar.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File

@Composable
actual fun rememberTasvirNegarGalleryLauncher(onResult: (String?) -> Unit): () -> Unit {
    val context = LocalContext.current
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            onResult(uri?.let { copyGalleryUriToCache(context, it) })
        }

    return {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

private fun copyGalleryUriToCache(
    context: Context,
    uri: Uri,
): String? =
    runCatching {
        val mimeType = context.contentResolver.getType(uri)
        val extension =
            when {
                mimeType?.contains("png", ignoreCase = true) == true -> "png"
                mimeType?.contains("webp", ignoreCase = true) == true -> "webp"
                else -> "jpg"
            }
        val cacheFile = File(context.cacheDir, "tasvir_pick_${System.currentTimeMillis()}.$extension")
        context.contentResolver.openInputStream(uri)?.use { input ->
            cacheFile.outputStream().use { output -> input.copyTo(output) }
        } ?: return null
        cacheFile.absolutePath
    }.getOrNull()
