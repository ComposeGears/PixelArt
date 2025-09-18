package io.github.composegears.pixelart.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.composegears.tiamat.compose.TiamatPreview
import com.composegears.tiamat.compose.navController
import com.composegears.tiamat.compose.navDestination
import com.composegears.tiamat.compose.replace
import io.github.composegears.pixelart.ui.common.PixelTheme
import io.github.composegears.pixelart.ui.layout.VerticalSpacer
import org.jetbrains.compose.ui.tooling.preview.Preview

val GridSetupScreen by navDestination {
    val navController = navController()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        var width by rememberSaveable { mutableStateOf("24") }
        var height by rememberSaveable { mutableStateOf("24") }

        val enabled by remember {
            derivedStateOf {
                width.isNotEmpty() && height.isNotEmpty()
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
        VerticalSpacer(8.dp)
        OutlinedButton(
            modifier = Modifier,
            enabled = enabled,
            onClick = {
                navController.replace(
                    entry = PixelDrawingScreen,
                    navArgs = GridSizeArgs(
                        width = width.toInt(),
                        height = height.toInt()
                    )
                )
            }
        ) {
            Text("Generate PixelGrid")
        }
    }
}

private fun String.isValidInput(max: Int = 2048) = all { it.isDigit() } && toInt() <= max

@Preview
@Composable
private fun GridSetupScreenPreview() = PixelTheme {
    TiamatPreview(GridSetupScreen)
}
