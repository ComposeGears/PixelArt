package io.github.composegears.pixelart.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.abs
import kotlin.math.roundToInt

@Suppress("CognitiveComplexMethod")
@Composable
fun PixelGrid(
    modifier: Modifier = Modifier,
    gridSize: Dp = 8.dp,
    drawFromTop: Boolean = false,
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

        if (drawFromTop) {
            extracted(horizontalSquares, verticalSquares, squareSize)
        } else {
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
}

private fun DrawScope.extracted(
    horizontalSquares: Int,
    verticalSquares: Int,
    squareSize: Float
) {
    for (i in 0..horizontalSquares) {
        for (j in 0..verticalSquares) {
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

@Preview
@Composable
private fun PixelGridPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PixelGrid(
            modifier = Modifier.size(200.dp),
            gridSize = 16.dp,
            drawFromTop = true
        )
        PixelGrid(
            modifier = Modifier.size(200.dp),
            gridSize = 8.dp,
            drawFromTop = true
        )

        PixelGrid(
            modifier = Modifier.size(200.dp),
            gridSize = 12.dp,
        )
    }
}
