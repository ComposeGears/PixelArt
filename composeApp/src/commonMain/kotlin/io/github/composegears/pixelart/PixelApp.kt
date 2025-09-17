package io.github.composegears.pixelart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.composegears.tiamat.compose.Navigation
import com.composegears.tiamat.compose.rememberNavController
import io.github.composegears.pixelart.screen.GifGridSetup
import io.github.composegears.pixelart.screen.GridSetupScreen
import io.github.composegears.pixelart.screen.IntroScreen
import io.github.composegears.pixelart.screen.PixelDrawingScreen
import io.github.composegears.pixelart.ui.common.PixelTheme

@Composable
fun PixelApp() = PixelTheme {
    Surface {
        val navController = rememberNavController(
            startDestination = IntroScreen
        )

        Navigation(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            destinations = arrayOf(
                IntroScreen,
                GifGridSetup,
                GridSetupScreen,
                PixelDrawingScreen
            ),
        )
    }
}