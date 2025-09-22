package io.github.composegears.pixelart.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composegears.tiamat.compose.*
import io.github.composegears.pixelart.screen.PixelDrawArgs.NewProjectArgs
import io.github.composegears.pixelart.ui.AppHeader
import io.github.composegears.pixelart.ui.common.PixelTheme
import io.github.composegears.pixelart.ui.layout.VerticalSpacer
import org.jetbrains.compose.ui.tooling.preview.Preview

val GridSetupScreen by navDestination {
    val navController = navController()

    Column(modifier = Modifier.fillMaxSize()) {
        AppHeader(title = "New project", onBack = navController::back)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var width by rememberSaveable { mutableStateOf("24") }
            var height by rememberSaveable { mutableStateOf("24") }
            var pixelSize by rememberSaveable { mutableStateOf("1") }

            val enabled by remember {
                derivedStateOf {
                    width.isNotEmpty() && height.isNotEmpty() && pixelSize.isNotEmpty()
                }
            }

            OutlinedTextField(
                value = width,
                label = {
                    Text(text = "Width")
                },
                onValueChange = { input ->
                    if (input.isValidInput()) {
                        width = input
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = height,
                label = {
                    Text(text = "Height")
                },
                singleLine = true,
                onValueChange = { input ->
                    if (input.isValidInput()) {
                        height = input
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = pixelSize,
                label = {
                    Text(text = "Pixel size")
                },
                singleLine = true,
                onValueChange = { input ->
                    if (input.isValidInput()) {
                        pixelSize = input
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            VerticalSpacer(8.dp)
            Button(
                modifier = Modifier,
                enabled = enabled,
                onClick = {
                    navController.replace(
                        entry = PixelDrawingScreen,
                        navArgs = NewProjectArgs(
                            pixelSize = pixelSize.toInt(),
                            width = width.toInt(),
                            height = height.toInt()
                        )
                    )
                }
            ) {
                Text(
                    text = "Generate PixelGrid",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun String.isValidInput(max: Int = 2048) = isEmpty() || all { it.isDigit() } && toInt() <= max

@Preview
@Composable
private fun GridSetupScreenPreview() = PixelTheme {
    TiamatPreview(GridSetupScreen)
}
