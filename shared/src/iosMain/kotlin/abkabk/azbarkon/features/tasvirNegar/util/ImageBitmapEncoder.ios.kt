@file:OptIn(ExperimentalForeignApi::class)

package abkabk.azbarkon.features.tasvirNegar.util

import abkabk.azbarkon.core.platform.toByteArray
import abkabk.azbarkon.core.platform.toNSData
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGDataProviderCreateWithCFData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageCreate
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.CoreGraphics.kCGImageAlphaLast
import platform.CoreGraphics.kCGRenderingIntentDefault
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.UIKit.imageWithCGImage

actual fun ImageBitmap.encodeToPngBytes(): ByteArray? =
    runCatching {
        val pixelMap = toPixelMap()
        val width = pixelMap.width
        val height = pixelMap.height
        if (width <= 0 || height <= 0) return null

        val argb = pixelMap.buffer
        val rgba = ByteArray(argb.size * 4)
        for (i in argb.indices) {
            val color = argb[i]
            val offset = i * 4
            rgba[offset] = ((color ushr 16) and 0xFF).toByte()
            rgba[offset + 1] = ((color ushr 8) and 0xFF).toByte()
            rgba[offset + 2] = (color and 0xFF).toByte()
            rgba[offset + 3] = ((color ushr 24) and 0xFF).toByte()
        }

        val provider = CGDataProviderCreateWithCFData(rgba.toNSData())
        val cgImage =
            CGImageCreate(
                width = width.toULong(),
                height = height.toULong(),
                bitsPerComponent = 8.toULong(),
                bitsPerPixel = 32.toULong(),
                bytesPerRow = (width * 4).toULong(),
                space = CGColorSpaceCreateDeviceRGB(),
                bitmapInfo = kCGImageAlphaLast or kCGBitmapByteOrder32Big,
                provider = provider,
                decode = null,
                shouldInterpolate = false,
                intent = kCGRenderingIntentDefault,
            ) ?: return null

        val pngData = UIImagePNGRepresentation(UIImage.imageWithCGImage(cgImage)) ?: return null
        pngData.toByteArray()
    }.getOrNull()
