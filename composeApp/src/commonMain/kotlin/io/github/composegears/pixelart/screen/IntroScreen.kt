package io.github.composegears.pixelart.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import com.composegears.tiamat.compose.navigate
import io.github.composegears.pixelart.ui.common.PixelTheme
import io.github.composegears.pixelart.ui.icons.DocumentScanner
import io.github.composegears.pixelart.ui.icons.Draw
import io.github.composegears.pixelart.ui.icons.PixelArtIcons
import io.github.composegears.pixelart.ui.util.TiamatDestinationPreview
import org.jetbrains.compose.ui.tooling.preview.Preview

val IntroScreen by navDestination {
    val navController = navController()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardRow(
            imageVector = PixelArtIcons.Draw,
            title = "New project",
            description = "Empty canvas",
            onClick = { navController.navigate(GridSetupScreen) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CardRow(
            imageVector = PixelArtIcons.DocumentScanner,
            title = "Import from Gif",
            description = "Import frames from a GIF file",
            onClick = { navController.navigate(GifPickerScreen) }
        )
    }
}

@Composable
private fun CardRow(
    imageVector: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .widthIn(min = 300.dp, max = 300.dp)
            .heightIn(max = 72.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = null
            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(text = title, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalContentColor.current.copy(alpha = 0.4f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun IntroScreenPreview() = PixelTheme {
    TiamatDestinationPreview(IntroScreen)
}