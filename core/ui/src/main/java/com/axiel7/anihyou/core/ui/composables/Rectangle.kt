package com.axiel7.anihyou.core.ui.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@Composable
fun Rectangle(
    width: Dp,
    height: Dp,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(width, height)) {
        drawIntoCanvas {
            drawRect(
                color = color,
                size = size
            )
        }
    }
}

@Composable
fun RoundedRectangle(
    width: Dp,
    height: Dp,
    color: Color,
    cornerRadius: CornerRadius,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.size(width, height)) {
        drawIntoCanvas {
            drawRoundRect(
                color = color,
                size = size,
                cornerRadius = cornerRadius
            )
        }
    }
}

@Preview
@Composable
fun RectanglePreview() {
    AniHyouTheme {
        Column {
            Rectangle(width = 50.dp, height = 12.dp, color = Color.Blue)
            RoundedRectangle(
                width = 50.dp,
                height = 12.dp,
                cornerRadius = CornerRadius(x = 16f, y = 16f),
                color = Color.Green,
            )
        }
    }
}