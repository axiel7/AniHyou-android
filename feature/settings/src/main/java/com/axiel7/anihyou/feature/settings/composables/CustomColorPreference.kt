package com.axiel7.anihyou.feature.settings.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.resources.ColorUtils.hexCode
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.common.CommonColorPickerDialog

@Composable
fun CustomColorPreference(
    color: Color?,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp)
) {
    val hexString = remember(color) { "#" + color?.hexCode?.drop(2).orEmpty() }
    var colorValue by remember { mutableStateOf(color) }
    var openDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 1.dp, bottom = 1.dp),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { openDialog = true },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.circle_fill_24),
                contentDescription = null,
                modifier = Modifier.padding(16.dp),
                tint = color ?: LocalContentColor.current,
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
    }

    if (openDialog) {
        CommonColorPickerDialog(
            title = stringResource(R.string.custom_color),
            initialColor = colorValue ?: MaterialTheme.colorScheme.primary,
            onDismissRequest = { openDialog = false },
            onColorSelected = { color ->
                colorValue = color
                onColorChanged(color)
            }
        )
    }
}