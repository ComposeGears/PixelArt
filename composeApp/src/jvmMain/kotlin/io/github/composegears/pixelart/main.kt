@file:Suppress("Filename")

package io.github.composegears.pixelart

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import io.github.vinceglb.filekit.FileKit

fun main() {
    System.setProperty("apple.awt.application.appearance", "system")

    FileKit.init(appId = "PixelArt")

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "PixelArt",
        ) {
            PixelApp()
        }
    }
}