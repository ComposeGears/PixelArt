package io.github.composegears.pixelart.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun PixelTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF1E66D6),
            onPrimary = Color.White,
            surface = Color(0xFF1E1F22),
            surfaceTint = Color.Gray,
        )
    ) {
        Surface(content = content)
    }
}