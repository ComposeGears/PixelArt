package io.github.composegears.pixelart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composegears.tiamat.compose.Navigation
import com.composegears.tiamat.compose.rememberNavController
import io.github.composegears.pixelart.screen.IntroScreen
import io.github.composegears.pixelart.screen.PixelDrawingScreen

@Composable
fun PixelApp() = MaterialTheme(colorScheme = darkColorScheme()) {
    Surface {
        val navController = rememberNavController(
            startDestination = IntroScreen
        )

        Navigation(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            destinations = arrayOf(
                IntroScreen,
                PixelDrawingScreen
            ),
        )
    }
}