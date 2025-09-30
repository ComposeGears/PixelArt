package io.github.composegears.pixelart.gif.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import com.shakster.gifkt.ImageFrame
import com.shakster.gifkt.compose.toImageBitmap

@Composable
fun rememberImageFrameBitmap(frame: ImageFrame): ImageBitmap = remember(frame) { frame.toImageBitmap() }