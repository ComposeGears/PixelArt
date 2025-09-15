package io.github.composegears.pixelart.gif.compose

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap
import com.shakster.gifkt.ImageFrame

actual fun ImageFrame.toImageBitmap(): ImageBitmap {
    val bitmap = createBitmap(width, height)
    bitmap.setPixels(argb, 0, width, 0, 0, width, height)
    return bitmap.asImageBitmap()
}
