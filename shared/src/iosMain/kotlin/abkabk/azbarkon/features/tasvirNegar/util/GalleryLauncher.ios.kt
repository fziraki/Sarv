@file:OptIn(ExperimentalForeignApi::class)

package abkabk.azbarkon.features.tasvirNegar.util

import abkabk.azbarkon.core.util.currentTimeMillis
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIImage
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceTypePhotoLibrary
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.UIApplication
import platform.darwin.NSObject

@Composable
actual fun rememberTasvirNegarGalleryLauncher(onResult: (String?) -> Unit): () -> Unit {
    val currentOnResult by rememberUpdatedState(onResult)
    val delegate = remember { GalleryPickerDelegate { currentOnResult(it) } }
    return remember(delegate) {
        {
            val picker = UIImagePickerController()
            picker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary
            picker.mediaTypes = listOf("public.image")
            picker.delegate = delegate
            UIApplication.sharedApplication.keyWindow?.rootViewController
                ?.presentViewController(picker, animated = true, completion = null)
        }
    }
}

// ponytail: UIImagePickerController is deprecated since iOS 14 (PHPicker is the
// replacement); it still works and its Kotlin delegate mapping is far simpler.
private class GalleryPickerDelegate(
    private val onResult: (String?) -> Unit,
) : NSObject(), UIImagePickerControllerDelegateProtocol {

    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>,
    ) {
        picker.dismissViewControllerAnimated(true, completion = null)
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val path = image?.let { saveImageToTemporaryFile(it) }
        onResult(path)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, completion = null)
        onResult(null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun saveImageToTemporaryFile(image: UIImage): String? {
    val pngData = UIImagePNGRepresentation(image) ?: return null
    val cacheDir =
        NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String ?: return null
    val path = "$cacheDir/gallery_sticker_${currentTimeMillis()}.png"
    return if (pngData.writeToFile(path, atomically = true)) path else null
}
