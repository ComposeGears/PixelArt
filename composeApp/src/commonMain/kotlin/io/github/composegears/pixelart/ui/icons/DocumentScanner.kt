package io.github.composegears.pixelart.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val PixelArtIcons.DocumentScanner: ImageVector
    get() {
        if (_DocumentScanner != null) {
            return _DocumentScanner!!
        }
        _DocumentScanner = ImageVector.Builder(
            name = "DocumentScanner",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF434343))) {
                moveTo(7f, 3f)
                horizontalLineTo(4f)
                verticalLineToRelative(3f)
                horizontalLineTo(2f)
                verticalLineTo(1f)
                horizontalLineToRelative(5f)
                verticalLineTo(3f)
                close()
                moveTo(22f, 6f)
                verticalLineTo(1f)
                horizontalLineToRelative(-5f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(3f)
                verticalLineToRelative(3f)
                horizontalLineTo(22f)
                close()
                moveTo(7f, 21f)
                horizontalLineTo(4f)
                verticalLineToRelative(-3f)
                horizontalLineTo(2f)
                verticalLineToRelative(5f)
                horizontalLineToRelative(5f)
                verticalLineTo(21f)
                close()
                moveTo(20f, 18f)
                verticalLineToRelative(3f)
                horizontalLineToRelative(-3f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(5f)
                verticalLineToRelative(-5f)
                horizontalLineTo(20f)
                close()
                moveTo(17f, 6f)
                horizontalLineTo(7f)
                verticalLineToRelative(12f)
                horizontalLineToRelative(10f)
                verticalLineTo(6f)
                close()
                moveTo(19f, 18f)
                curveToRelative(0f, 1.1f, -0.9f, 2f, -2f, 2f)
                horizontalLineTo(7f)
                curveToRelative(-1.1f, 0f, -2f, -0.9f, -2f, -2f)
                verticalLineTo(6f)
                curveToRelative(0f, -1.1f, 0.9f, -2f, 2f, -2f)
                horizontalLineToRelative(10f)
                curveToRelative(1.1f, 0f, 2f, 0.9f, 2f, 2f)
                verticalLineTo(18f)
                close()
                moveTo(15f, 8f)
                horizontalLineTo(9f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(6f)
                verticalLineTo(8f)
                close()
                moveTo(15f, 11f)
                horizontalLineTo(9f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(6f)
                verticalLineTo(11f)
                close()
                moveTo(15f, 14f)
                horizontalLineTo(9f)
                verticalLineToRelative(2f)
                horizontalLineToRelative(6f)
                verticalLineTo(14f)
                close()
            }
        }.build()

        return _DocumentScanner!!
    }

@Suppress("ObjectPropertyName")
private var _DocumentScanner: ImageVector? = null
