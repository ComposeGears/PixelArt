package io.github.composegears.pixelart.gif.compose

import androidx.compose.ui.graphics.ImageBitmap
import com.shakster.gifkt.ImageFrame

expect fun ImageFrame.toImageBitmap(): ImageBitmap
