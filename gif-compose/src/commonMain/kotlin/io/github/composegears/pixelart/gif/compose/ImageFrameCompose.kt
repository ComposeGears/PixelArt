package io.github.composegears.pixelart.gif.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import com.shakster.gifkt.ImageFrame

expect fun ImageFrame.toImageBitmap(): ImageBitmap

@Composable
fun rememberImageFrameBitmap(frame: ImageFrame): ImageBitmap = remember(frame) { frame.toImageBitmap() }