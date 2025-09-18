@file:Suppress("NOTHING_TO_INLINE")

package io.github.composegears.pixelart.ui.layout

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
inline fun ColumnScope.VerticalSpacer(
    dp: Dp,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = modifier.height(dp))
}

@Composable
inline fun RowScope.HorizontalSpacer(
    dp: Dp,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = modifier.width(dp))
}

@Composable
inline fun SizeSpacer(
    size: Dp,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = modifier.size(size))
}

@Composable
inline fun RowScope.WeightSpacer(
    modifier: Modifier = Modifier,
    weight: Float = 1f,
) {
    Spacer(modifier = modifier.weight(weight))
}

@Composable
inline fun ColumnScope.WeightSpacer(
    modifier: Modifier = Modifier,
    weight: Float = 1f,
) {
    Spacer(modifier = modifier.weight(weight))
}