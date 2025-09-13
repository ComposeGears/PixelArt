package io.github.composegears.pixelart

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import com.composegears.tiamat.compose.Navigation
import com.composegears.tiamat.compose.rememberNavController
import com.composegears.tiamat.navigation.NavDestination

@Composable
fun PixelTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface(content = content)
    }
}

@Composable
fun NavigationPreview(destination: NavDestination<*>) {
    Navigation(
        navController = rememberNavController(startDestination = destination),
        destinations = arrayOf(destination)
    )
}
