package io.github.composegears.pixelart.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import io.github.composegears.pixelart.core.rememberMutableState
import io.github.composegears.pixelart.gif.compose.toImageBitmap
import io.github.composegears.pixelart.ui.PixelGrid
import io.github.composegears.pixelart.ui.layout.WeightSpacer
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

val GifGridSetup by navDestination<PlatformFile> {
    val navController = navController()
    val file = navArgs()

    val viewModel = viewModel { GifGridSetupViewModel(file) }
    val images by viewModel.frames.collectAsState()

    if (images.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(onClick = navController::back) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )
            }

            var selectedFrame by rememberMutableState(images) { images.first() }
            var pixelSize by rememberMutableState { 1f }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(selectedFrame.width.dp, selectedFrame.height.dp)
                ) {
                    PixelGrid(
                        modifier = Modifier.matchParentSize(),
                        gridSize = pixelSize.roundToInt().dp,
                        drawFromTop = true
                    )
                    Image(
                        modifier = Modifier
                            .matchParentSize()
                            .align(Alignment.Center),
                        bitmap = selectedFrame.toImageBitmap(),
                        contentDescription = null
                    )
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp),
                    text = "Adjust the slider to find GIF pixel size",
                    maxLines = 1
                )
                WeightSpacer()
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                ) {
                    var valueRange = 1f..42f
                    Slider(
                        value = pixelSize,
                        onValueChange = {
                            pixelSize = it
                        },
                        valueRange = valueRange,
                    )
                    Row {
                        Text(text = pixelSize.roundToInt().toString())
                        WeightSpacer()
                        Text(text = valueRange.endInclusive.roundToInt().toString())
                    }
                }
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
