package com.axiel7.anihyou.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltipBox
import androidx.compose.material3.PlainTooltipState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberPlainTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

@Composable
fun TextIconHorizontal(
    text: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    iconPadding: PaddingValues = PaddingValues(end = 8.dp),
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.padding(iconPadding),
            tint = color
        )
        Text(
            text = text,
            color = color,
            fontSize = fontSize
        )
    }
}

@Composable
fun TextIconVertical(
    text: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    fontSize: TextUnit = TextUnit.Unspecified,
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = text,
            modifier = Modifier.padding(4.dp),
            tint = color
        )
        Text(
            text = text,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .defaultPlaceholder(visible = isLoading),
            color = color,
            fontSize = fontSize
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextIconVertical(
    text: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    tooltip: String,
    isLoading: Boolean = false,
) {
    val tooltipState = rememberPlainTooltipState()
    val scope = rememberCoroutineScope()

    PlainTooltipBox(
        tooltip = { Text(text = tooltip) },
        tooltipState = tooltipState
    ) {
        TextIconVertical(
            text = text,
            icon = icon,
            modifier = modifier.clickable { scope.launch { tooltipState.show() } },
            isLoading = isLoading
        )
    }
}

@Composable
fun TextSubtitleVertical(
    text: String?,
    subtitle: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .defaultPlaceholder(visible = isLoading),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text ?: stringResource(R.string.unknown),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            color = MaterialTheme.colorScheme.outline,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
    }
}

@Preview
@Composable
fun TextSubtitleVerticalPreview() {
    AniHyouTheme {
        Surface {
            TextSubtitleVertical(
                text = "89%",
                subtitle = "Mean score"
            )
        }
    }
}

@Preview
@Composable
fun TextIconHorizontalPreview() {
    AniHyouTheme {
        Surface {
            TextIconHorizontal(text = "This is an example", icon = R.drawable.star_filled_20)
        }
    }
}

@Preview
@Composable
fun TextIconVerticalPreview() {
    AniHyouTheme {
        Surface {
            TextIconVertical(text = "This is an example", icon = R.drawable.star_filled_20)
        }
    }
}