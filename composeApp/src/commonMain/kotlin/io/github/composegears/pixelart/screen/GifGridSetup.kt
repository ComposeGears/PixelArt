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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.composegears.tiamat.compose.*
import com.shakster.gifkt.GifDecoder
import com.shakster.gifkt.ImageFrame
import io.github.composegears.pixelart.core.rememberMutableState
import io.github.composegears.pixelart.gif.compose.rememberImageFrameBitmap
import io.github.composegears.pixelart.screen.PixelDrawArgs.GifImportArgs
import io.github.composegears.pixelart.screen.State.GifGridSetupState
import io.github.composegears.pixelart.ui.AppHeader
import io.github.composegears.pixelart.ui.PixelGrid
import io.github.composegears.pixelart.ui.common.PixelTheme
import io.github.composegears.pixelart.ui.layout.VerticalSpacer
import io.github.composegears.pixelart.ui.layout.WeightSpacer
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt
import kotlin.time.Duration

val GifGridSetup by navDestination<PlatformFile> {
    val navController = navController()
    val file = navArgs()

    val viewModel = viewModel { GifGridSetupViewModel(file) }
    val state by viewModel.state.collectAsState()

    when (val state = state) {
        is GifGridSetupState -> GifGridSetupUI(
            state = state,
            onBack = navController::back,
            openDrawing = {
                navController.replace(
                    entry = PixelDrawingScreen,
                    navArgs = it
                )
            }
        )
        is State.None -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun GifGridSetupUI(
    state: GifGridSetupState,
    onBack: () -> Unit,
    openDrawing: (GifImportArgs) -> Unit
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val frames = state.frames

    Box(modifier = Modifier.fillMaxSize()) {
        var selectedFrame by rememberMutableState(frames) { frames.first() }
        var pixelSize by rememberMutableState { 8f }

        if (windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            Row(modifier = Modifier.fillMaxSize()) {
                VerticalFrames(
                    frames = frames,
                    selectedFrame = selectedFrame,
                    onSelect = { selectedFrame = it }
                )
                VerticalDivider()
                Column {
                    AppHeader(
                        title = "GIF pixel size",
                        onBack = onBack
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
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
                                Button(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    onClick = {
                                        openDrawing(
                                            GifImportArgs(
                                                pixelSize = pixelSize.roundToInt(),
                                                width = state.width,
                                                height = state.height,
                                                frames = frames
                                            )
                                        )
                                    }
                                ) {
                                    Text(
                                        text = "Continue",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
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
                    onBack = onBack
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
                        Button(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            onClick = {
                                openDrawing(
                                    GifImportArgs(
                                        pixelSize = pixelSize.roundToInt(),
                                        width = state.width,
                                        height = state.height,
                                        frames = frames
                                    )
                                )
                            }
                        ) {
                            Text(
                                text = "Continue",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
                WeightSpacer()
                HorizontalDivider()
                HorizontalFrames(
                    frames = frames,
                    selectedFrame = selectedFrame,
                    onSelect = { selectedFrame = it }
                )
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
        modifier = modifier
            .clipToBounds()
            .zoomable(rememberZoomState()),
        contentAlignment = Alignment.Center
    ) {
        PixelGrid(
            modifier = Modifier.matchParentSize(),
            gridSize = pixelSize.roundToInt().dp,
            drawFromTop = true
        )
        Image(
            modifier = Modifier
                .requiredSize(width = frame.width.dp, height = frame.height.dp)
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
        Badge(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp),
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Text(text = "${frame.index + 1}", fontSize = 9.sp)
        }
    }
}

@Preview
@Composable
private fun GifGridSetupPreview() = PixelTheme {
    GifGridSetupUI(
        state = GifGridSetupState(
            width = 0,
            height = 0,
            frames = listOf(
                ImageFrame(
                    argb = intArrayOf(0, 0, 0, 0),
                    width = 1,
                    height = 1,
                    duration = Duration.ZERO,
                    timestamp = Duration.ZERO,
                    index = 0
                )
            )
        ),
        onBack = {},
        openDrawing = {}
    )
}

class GifGridSetupViewModel(
    private val file: PlatformFile
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.None)
    val state = _state.asStateFlow()

    init {
        readGif()
    }

    private fun readGif() {
        viewModelScope.launch(Dispatchers.Default) {
            val decoder = GifDecoder(file.readBytes())
            decoder.use {
                _state.update {
                    GifGridSetupState(
                        width = decoder.width,
                        height = decoder.height,
                        frames = decoder.asList()
                    )
                }
            }
        }
    }
}

sealed interface State {
    data object None : State

    data class GifGridSetupState(
        val width: Int,
        val height: Int,
        val frames: List<ImageFrame>
    ) : State
}