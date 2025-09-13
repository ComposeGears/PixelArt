package io.github.composegears.pixelart.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.composegears.tiamat.compose.back
import com.composegears.tiamat.compose.navArgs
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.painterResource
import pixelart.composeapp.generated.resources.Res
import pixelart.composeapp.generated.resources.frame_0
import kotlin.math.roundToInt

val PixelDrawingScreen by navDestination<GridSizeArgs> {
    val navController = navController()
    val navArgs = navArgs()

    Column(modifier = Modifier.fillMaxSize()) {
        IconButton(onClick = { navController.back() }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
        Text("width=${navArgs.width}, height=${navArgs.height}")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .zoomable(rememberZoomState(initialScale = 1f)),
        ) {
            Box(
                modifier = Modifier
                    .background(Color.LightGray)
            ) {
                Image(
                    painter = painterResource(Res.drawable.frame_0),
                    contentDescription = null
                )

                val textMeasurer = rememberTextMeasurer()
                val pixelSize = 16f

                Canvas(
                    modifier = Modifier
                        .matchParentSize()
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
        }
    }
}

data class GridSizeArgs(
    val width: Int,
    val height: Int
)
