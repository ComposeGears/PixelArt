package io.github.composegears.pixelart.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.composegears.tiamat.compose.back
import com.composegears.tiamat.compose.navArgs
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import com.shakster.gifkt.GifDecoder
import com.shakster.gifkt.ImageFrame
import io.github.composegears.pixelart.gif.compose.toImageBitmap
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

val GifGridSetup by navDestination<PlatformFile> {
    val navController = navController()
    val file = navArgs()

    val viewModel = viewModel { GifGridSetupViewModel(file) }
    val images by viewModel.frames.collectAsState()

    if (images.isNotEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            IconButton(onClick = navController::back) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }

            var selectedFrame by remember(images) { mutableStateOf(images.first()) }
            var text by remember { mutableStateOf("") }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Image(
                    modifier = Modifier
                        .size(300.dp)
                        .align(Alignment.Center),
                    bitmap = selectedFrame.toImageBitmap(),
                    contentDescription = null
                )
                Text(text = text)
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(images) { item ->
                    ImageItem(
                        frame = item,
                        selected = item == selectedFrame,
                        onClick = {
                            selectedFrame = item
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageItem(
    frame: ImageFrame,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val border = when {
        selected -> Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(12.dp)
        )
        else -> Modifier
    }
    Box(
        modifier = modifier
            .size(150.dp)
            .then(border)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        PixelGrid(modifier = Modifier.matchParentSize())
        Image(
            modifier = Modifier.matchParentSize(),
            bitmap = frame.toImageBitmap(),
            contentDescription = null
        )
    }
}

@Composable
private fun PixelGrid(
    modifier: Modifier = Modifier,
    gridSize: Dp = 8.dp,
) {
    Canvas(modifier = modifier.clipToBounds()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        val squareSize = gridSize.toPx()

        val verticalSquares = (canvasHeight / squareSize).roundToInt()
        val horizontalSquares = (canvasWidth / squareSize).roundToInt()

        drawRect(
            color = Color.White,
            topLeft = Offset(x = 0f, y = 0f),
            size = Size(width = canvasWidth, height = canvasHeight),
        )
        translate(
            left = canvasWidth / 2f + squareSize / 2f,
            top = canvasHeight / 2f + squareSize / 2f,
        ) {
            for (i in -horizontalSquares / 2 - 2..horizontalSquares / 2) {
                for (j in -verticalSquares / 2 - 2..verticalSquares / 2) {
                    if (abs(i % 2) == abs(j % 2)) {
                        drawRect(
                            color = Color.LightGray,
                            topLeft = Offset(x = i * squareSize, y = j * squareSize),
                            size = Size(width = squareSize, height = squareSize),
                        )
                    }
                }
            }
        }
    }
}

class GifGridSetupViewModel(
    private val file: PlatformFile
) : ViewModel() {

    private val _frames = MutableStateFlow(emptyList<ImageFrame>())
    val frames = _frames.asStateFlow()

    init {
        extractFrames()
    }

    private fun extractFrames() {
        viewModelScope.launch(Dispatchers.Default) {
            val decoder = GifDecoder(file.readBytes())

            decoder.use {
                _frames.update { decoder.asList() }
            }
        }
    }
}
