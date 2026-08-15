@file:OptIn(ExperimentalForeignApi::class)

package abkabk.azbarkon.features.profile.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerDelegateProtocol
import platform.UIKit.UIDocumentPickerViewController
import platform.UniformTypeIdentifiers.UTTypeJSON
import platform.darwin.NSObject

@Composable
actual fun rememberBackupImportLauncher(onResult: (String?) -> Unit): () -> Unit {
    val currentOnResult by rememberUpdatedState(onResult)
    val delegate = remember { BackupFilePickerDelegate { currentOnResult(it) } }
    return remember(delegate) {
        {
            val picker =
                UIDocumentPickerViewController(
                    forOpeningContentTypes = listOf(UTTypeJSON),
                    asCopy = true,
                )
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}

private class BackupFilePickerDelegate(
    private val onResult: (String?) -> Unit,
) : NSObject(), UIDocumentPickerDelegateProtocol {

    override fun documentPicker(
        controller: UIDocumentPickerViewController,
        didPickDocumentsAtURLs: List<*>,
    ) {
        controller.dismissViewControllerAnimated(true, completion = null)
        val path = (didPickDocumentsAtURLs.firstOrNull() as? NSURL)?.path
        onResult(path?.let { readFileContent(it) })
    }

    override fun documentPickerWasCancelled(controller: UIDocumentPickerViewController) {
        controller.dismissViewControllerAnimated(true, completion = null)
        onResult(null)
    }
}

private fun readFileContent(path: String): String? =
    NSString.stringWithContentsOfFile(path, NSUTF8StringEncoding, null)