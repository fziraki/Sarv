@file:OptIn(ExperimentalForeignApi::class)

package abkabk.azbarkon.features.tasvirNegar.util

import abkabk.azbarkon.core.platform.toByteArray
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreFoundation.CFDataCreate
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreGraphics.CGColorSpaceCreateDeviceRGB
import platform.CoreGraphics.CGDataProviderCreateWithCFData
import platform.CoreGraphics.CGImageAlphaInfo
import platform.CoreGraphics.CGImageCreate
import platform.CoreGraphics.CGColorRenderingIntent
import platform.CoreGraphics.kCGBitmapByteOrder32Big
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import kotlinx.cinterop.UByteVarOf
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned

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

        val provider = rgba.usePinned { pinned ->
            val cfData = CFDataCreate(kCFAllocatorDefault, pinned.addressOf(0).reinterpret<UByteVarOf<UByte>>(), rgba.size.toLong())
            CGDataProviderCreateWithCFData(cfData)
        }
        val cgImage =
            CGImageCreate(
                width = width.toULong(),
                height = height.toULong(),
                bitsPerComponent = 8.toULong(),
                bitsPerPixel = 32.toULong(),
                bytesPerRow = (width * 4).toULong(),
                space = CGColorSpaceCreateDeviceRGB(),
                bitmapInfo = CGImageAlphaInfo.kCGImageAlphaLast.value or kCGBitmapByteOrder32Big,
                provider = provider,
                decode = null,
                shouldInterpolate = false,
                intent = CGColorRenderingIntent.kCGRenderingIntentDefault,
            ) ?: return null

        val pngData = UIImagePNGRepresentation(UIImage(cgImage)) ?: return null
        pngData.toByteArray()
    }.getOrNull()
