package io.github.composegears.pixelart

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "PixelArt",
    ) {
        PixelApp()
    }
}