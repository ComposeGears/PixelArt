package io.github.composegears.pixelart.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.composegears.tiamat.compose.navDestination
import com.shakster.gifkt.GifDecoder
import com.shakster.gifkt.ImageFrame
import io.github.composegears.pixelart.gif.compose.toImageBitmap
import io.github.composegears.pixelart.ui.common.PixelTheme
import io.github.composegears.pixelart.ui.util.TiamatDestinationPreview
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.ui.tooling.preview.Preview

val GifPickerScreen by navDestination {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val coroutineScope = rememberCoroutineScope()

        var file by remember { mutableStateOf<PlatformFile?>(null) }
        var images by remember { mutableStateOf<List<ImageFrame>?>(null) }

        LaunchedEffect(file) {
            val file = file ?: return@LaunchedEffect

            val decoder = GifDecoder(file.readBytes())
            images = decoder.asList()
            decoder.close()
        }

        Button(onClick = {
            coroutineScope.launch {
                withContext(Dispatchers.Default) {
                    val pickerFile = FileKit.openFilePicker(type = FileKitType.File(extensions = listOf("gif")))
                    file = pickerFile
                }
            }
        }) {
            Text("Pick GIF")
        }

        images?.let { images ->
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                images.forEach {
                    Image(
                        bitmap = it.toImageBitmap(),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun GifImportScreenPreview() = PixelTheme {
    TiamatDestinationPreview(GifPickerScreen)
}
