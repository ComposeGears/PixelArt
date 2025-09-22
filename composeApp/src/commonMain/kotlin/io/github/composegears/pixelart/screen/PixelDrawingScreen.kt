package io.github.composegears.pixelart.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.composegears.tiamat.compose.*
import com.shakster.gifkt.ImageFrame
import io.github.composegears.pixelart.ui.AdaptiveLayout
import io.github.composegears.pixelart.ui.AppHeader
import io.github.composegears.pixelart.ui.common.PixelTheme
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pixelart.composeapp.generated.resources.Res
import pixelart.composeapp.generated.resources.frame_0
import kotlin.math.roundToInt

val PixelDrawingScreen by navDestination<PixelDrawArgs> {
    val navController = navController()
    val navArgs = navArgs()

    PixelDrawingUi(
        onBack = navController::back
    )
}

@Composable
private fun PixelDrawingUi(onBack: () -> Unit) {
    AdaptiveLayout(
        phone = {
            PhoneLayout(onBack = onBack)
        },
        tablet = {
            TabletLayout(onBack = onBack)
        }
    )
}

@Composable
private fun PhoneLayout(onBack: () -> Unit) {
    Column {
        AppHeader(title = "Drawing", onBack = onBack)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .zoomable(rememberZoomState())
            ) {
                Box(modifier = Modifier.background(Color.LightGray)) {
                    Image(
                        painter = painterResource(Res.drawable.frame_0),
                        contentDescription = null
                    )
                    DrawingArea(modifier = Modifier.matchParentSize())
                }
            }
        }
    }
}

@Composable
private fun TabletLayout(onBack: () -> Unit) {
    Column {
        AppHeader(title = "Drawing", onBack = onBack)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .zoomable(rememberZoomState())
            ) {
                Box(modifier = Modifier.background(Color.LightGray)) {
                    Image(
                        painter = painterResource(Res.drawable.frame_0),
                        contentDescription = null
                    )
                    DrawingArea(modifier = Modifier.matchParentSize())
                }
            }
        }
    }
}

@Composable
private fun DrawingArea(modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val pixelSize = 16f

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(true)
                    val col = (down.position.x / pixelSize).toInt()
                    val row = (down.position.y / pixelSize).toInt()

                    println("Position: x=$row, y=$col")
                }
            }
    ) {
        val lines = (size.width / pixelSize).roundToInt()

        for (i in 0..<lines) {
            drawText(
                textMeasurer = textMeasurer,
                text = when {
                    i < 10 -> "  $i"
                    else -> "$i"
                },
                topLeft = Offset(i * pixelSize, -pixelSize),
                style = TextStyle(fontSize = 4.sp, color = Color.White),
                softWrap = false
            )
        }
        for (i in 0..<lines) {
            drawText(
                textMeasurer = textMeasurer,
                text = when {
                    i < 10 -> "   $i"
                    else -> "$i"
                },
                topLeft = Offset(-pixelSize, i * pixelSize + 2),
                style = TextStyle(fontSize = 4.sp, color = Color.White),
            )
        }

        /* for (i in 0..<lines) {
         for (j in 0..<lines) {
             if (abs(i % 2) == abs(j % 2)) {
                 drawRect(
                     color = Color.Blue.copy(alpha = 0.2f),
                     topLeft = Offset(x = i * pixelSize, y = j * pixelSize),
                     size = Size(width = pixelSize, height = pixelSize),
                 )
             }
         }
     }*/

        for (i in 0..lines) {
            drawLine(
                color = Color.DarkGray,
                start = Offset(i * pixelSize, 0f),
                end = Offset(i * pixelSize, size.height),
            )
            drawLine(
                color = Color.DarkGray,
                start = Offset(0f, i * pixelSize),
                end = Offset(size.height, i * pixelSize),
            )
        }
    }
}

sealed interface PixelDrawArgs {
    data class NewProjectArgs(
        val pixelSize: Int,
        val width: Int,
        val height: Int
    ) : PixelDrawArgs

    data class GifImportArgs(
        val pixelSize: Int,
        val width: Int,
        val height: Int,
        val frames: List<ImageFrame>,
    ) : PixelDrawArgs
}

@Preview
@Composable
private fun PixelDrawingScreenPreview() = PixelTheme {
    TiamatPreview(
        destination = PixelDrawingScreen,
        navArgs = PixelDrawArgs.NewProjectArgs(
            pixelSize = 2,
            width = 32,
            height = 32
        )
    )
}