package com.axiel7.anihyou.ui.screens.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.utils.ColorUtils.colorFromHex
import com.axiel7.anihyou.utils.ColorUtils.hexCode

@Composable
fun CustomColorPreference(
    color: Color?,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val hexString = remember(color) { "#" + color?.hexCode?.drop(2).orEmpty() }
    var hexValue by remember { mutableStateOf(hexString.drop(1)) }
    var colorValue by remember { mutableStateOf(color) }
    var openDialog by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { openDialog = true },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .padding(16.dp)
                .size(24.dp)
        )

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.custom_color),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (color != null) {
                Text(
                    text = hexString,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
        }
    }

    if (openDialog) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        AlertDialog(
            onDismissRequest = { openDialog = false },
            title = { Text(text = stringResource(R.string.custom_color)) },
            text = {
                OutlinedTextField(
                    value = hexValue,
                    onValueChange = { value ->
                        hexValue = value.replace("#", "")
                        colorFromHex("#$hexValue")?.let { color ->
                            colorValue = color
                        }
                    },
                    modifier = Modifier.focusRequester(focusRequester),
                    label = { Text(text = "HEX") },
                    prefix = { Text(text = "#") },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        colorValue?.let { onColorChanged(it) }
                    }
                ) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }
}