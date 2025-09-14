package io.github.composegears.pixelart.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.composegears.tiamat.compose.navDestination
import io.github.composegears.pixelart.ui.common.PixelTheme
import io.github.composegears.pixelart.ui.util.TiamatDestinationPreview
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.name
import org.jetbrains.compose.ui.tooling.preview.Preview

val GifPickerScreen by navDestination {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var name by remember { mutableStateOf<String?>(null) }

        val singleLauncher = rememberFilePickerLauncher(
            mode = FileKitMode.Single,
            type = FileKitType.File(extensions = listOf("gif"))
        ) { file ->
            name = file?.name.orEmpty()
        }

        Button(onClick = { singleLauncher.launch() }) {
            Text("Pick GIF")
        }
        if (!name.isNullOrEmpty()) {
            Text(text = "file name: $name")
        }
    }
}

@Preview
@Composable
private fun GifImportScreenPreview() = PixelTheme {
    TiamatDestinationPreview(GifPickerScreen)
}
