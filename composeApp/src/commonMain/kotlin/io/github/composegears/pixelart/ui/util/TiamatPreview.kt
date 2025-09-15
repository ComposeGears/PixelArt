package io.github.composegears.pixelart.ui.util

import androidx.compose.runtime.Composable
import com.composegears.tiamat.compose.Navigation
import com.composegears.tiamat.compose.rememberNavController
import com.composegears.tiamat.navigation.NavDestination
import com.composegears.tiamat.navigation.NavDestination.Companion.toNavEntry

@Composable
fun <T : Any> TiamatDestinationPreview(destination: NavDestination<T>, navArgs: T? = null) {
    Navigation(
        navController = rememberNavController(
            startEntry = destination.toNavEntry(navArgs = navArgs),
        ),
        destinations = arrayOf(destination)
    )
}
