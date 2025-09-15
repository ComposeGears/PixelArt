package io.github.composegears.pixelart.gif.compose

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.shakster.gifkt.ImageFrame
import org.jetbrains.skia.*
import org.jetbrains.skia.impl.use

actual fun ImageFrame.toImageBitmap(): ImageBitmap {
    val imageInfo = ImageInfo(
        width = width,
        height = height,
        colorType = ColorType.RGBA_8888,
        alphaType = ColorAlphaType.PREMUL
    )

    val byteArray = convertArgbToRgba(argb)

    return Bitmap().use { skiaBitmap ->
        skiaBitmap.allocPixels(imageInfo)
        skiaBitmap.installPixels(byteArray)

        Image.makeFromBitmap(skiaBitmap).use { image ->
            image.toComposeImageBitmap()
        }
    }
}

/**
 * Efficiently converts ARGB array to RGBA byte array.
 * Uses bit manipulation for better performance.
 */
private fun convertArgbToRgba(argb: IntArray): ByteArray {
    val byteArray = ByteArray(argb.size * 4)

    for (i in argb.indices) {
        val color = argb[i]
        val base = i * 4

        // Extract components using bit shifts for RGBA format
        byteArray[base] = (color shr 16).toByte()      // R
        byteArray[base + 1] = (color shr 8).toByte()   // G
        byteArray[base + 2] = color.toByte()           // B
        byteArray[base + 3] = (color shr 24).toByte()  // A
    }

    return byteArray
}