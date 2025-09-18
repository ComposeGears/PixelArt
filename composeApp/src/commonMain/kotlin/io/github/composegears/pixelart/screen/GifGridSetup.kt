package io.github.composegears.pixelart.screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.composegears.tiamat.compose.back
import com.composegears.tiamat.compose.navArgs
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import com.shakster.gifkt.GifDecoder
import com.shakster.gifkt.ImageFrame
import io.github.composegears.pixelart.core.rememberMutableState
import io.github.composegears.pixelart.gif.compose.rememberImageFrameBitmap
import io.github.composegears.pixelart.ui.AppHeader
import io.github.composegears.pixelart.ui.PixelGrid
import io.github.composegears.pixelart.ui.layout.VerticalSpacer
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

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val viewModel = viewModel { GifGridSetupViewModel(file) }
    val images by viewModel.frames.collectAsState()

    if (images.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize()) {
            var selectedFrame by rememberMutableState(images) { images.first() }
            var pixelSize by rememberMutableState { 8f }

            if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
                Row(modifier = Modifier.fillMaxSize()) {
                    VerticalFrames(
                        frames = images,
                        selectedFrame = selectedFrame,
                        onSelect = { selectedFrame = it }
                    )
                    VerticalDivider()
                    Column {
                        AppHeader(
                            title = "GIF pixel size",
                            onBack = navController::back
                        )
                        Column(modifier = Modifier.fillMaxSize()) {
                            WeightSpacer()
                            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    FrameGrid(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        frame = selectedFrame,
                                        pixelSize = pixelSize
                                    )
                                    VerticalSpacer(16.dp)
                                    Text(
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        text = "Adjust the slider to find real GIF pixel size",
                                        overflow = TextOverflow.Ellipsis,
                                        maxLines = 1
                                    )
                                    SliderSection(
                                        modifier = Modifier.widthIn(max = 400.dp),
                                        pixelSize = pixelSize,
                                        onSizeChange = { pixelSize = it }
                                    )
                                }
                            }
                            WeightSpacer()
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    AppHeader(
                        title = "GIF pixel size",
                        onBack = navController::back
                    )
                    WeightSpacer()
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            FrameGrid(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                frame = selectedFrame,
                                pixelSize = pixelSize
                            )
                            VerticalSpacer(16.dp)
                            Text(
                                modifier = Modifier.padding(horizontal = 4.dp),
                                text = "Adjust the slider to find real GIF pixel size",
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            SliderSection(
                                modifier = Modifier.widthIn(max = 400.dp),
                                pixelSize = pixelSize,
                                onSizeChange = { pixelSize = it }
                            )
                        }
                    }
                    WeightSpacer()
                    HorizontalDivider()
                    HorizontalFrames(
                        frames = images,
                        selectedFrame = selectedFrame,
                        onSelect = { selectedFrame = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun FrameGrid(
    frame: ImageFrame,
    pixelSize: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(width = frame.width.dp, height = frame.height.dp),
        contentAlignment = Alignment.Center
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
            bitmap = rememberImageFrameBitmap(frame),
            contentDescription = null
        )
    }
}

@Composable
private fun SliderSection(
    pixelSize: Float,
    onSizeChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        val valueRange = 1f..42f

        Slider(
            value = pixelSize,
            onValueChange = onSizeChange,
            valueRange = valueRange,
        )
        Row {
            Text(text = pixelSize.roundToInt().toString())
            WeightSpacer()
            Text(text = valueRange.endInclusive.roundToInt().toString())
        }
    }
}

@Composable
private fun VerticalFrames(
    frames: List<ImageFrame>,
    selectedFrame: ImageFrame,
    onSelect: (ImageFrame) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 150.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(frames) { item ->
            ImageItem(
                frame = item,
                selected = item == selectedFrame,
                onClick = { onSelect(item) }
            )
        }
    }
}

@Composable
private fun HorizontalFrames(
    frames: List<ImageFrame>,
    selectedFrame: ImageFrame,
    onSelect: (ImageFrame) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 120.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(frames) { item ->
            ImageItem(
                frame = item,
                selected = item == selectedFrame,
                onClick = { onSelect(item) }
            )
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
            width = 2.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(12.dp)
        )
        else -> Modifier
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .aspectRatio(frame.width.toFloat() / frame.height.toFloat())
            .clickable(onClick = onClick)
            .then(border)
    ) {
        PixelGrid(
            modifier = Modifier
                .matchParentSize()
                .padding(1.dp)
                .clip(RoundedCornerShape(12.dp))
        )
        Image(
            modifier = Modifier.matchParentSize(),
            bitmap = rememberImageFrameBitmap(frame),
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
