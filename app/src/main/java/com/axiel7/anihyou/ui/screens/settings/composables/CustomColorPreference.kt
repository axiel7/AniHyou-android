package com.axiel7.anihyou.ui.screens.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.common.DialogWithTextInput
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
        DialogWithTextInput(
            title = stringResource(R.string.custom_color),
            label = "HEX",
            prefix = "#",
            value = hexValue,
            onValueChange = { value ->
                hexValue = value.replace("#", "")
                colorFromHex("#$hexValue")?.let { color ->
                    colorValue = color
                }
            },
            onConfirm = {
                openDialog = false
                colorValue?.let { onColorChanged(it) }
            },
            onDismiss = { openDialog = false }
        )
    }
}