package com.axiel7.anihyou.ui.composables.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun SmallCircularProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    CircularProgressIndicator(
        modifier = modifier.size(20.dp),
        color = color,
        strokeWidth = 3.dp
    )
}

@Preview
@Composable
fun ProgressIndicatorPreview() {
    AniHyouTheme {
        Surface {
            SmallCircularProgressIndicator()
        }
    }
}