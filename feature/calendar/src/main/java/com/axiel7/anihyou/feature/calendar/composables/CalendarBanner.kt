package com.axiel7.anihyou.feature.calendar.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.common.utils.DateUtils.toLocalized
import com.axiel7.anihyou.core.ui.composables.CalendarBannerView
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import java.time.LocalDateTime

@Composable
fun CalendarBanner(
    title: String,
    date: LocalDateTime,
    modifier: Modifier = Modifier,
    height: Dp,
    imageUrl: String? = null,
    color: Color? = null,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        CalendarBannerView(
            imageUrl = imageUrl,
            height = height,
            modifier = Modifier.fillMaxWidth(),
            gradientColor = color?.copy(alpha = 0.6f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            // TODO `contentColorFor` does not work with custom colors
            Text(
                text = date.toLocalized(pattern = "d MMM") ?: "",
                style = MaterialTheme.typography.labelMedium,
                color = contentColorFor(color ?: MaterialTheme.colorScheme.outline)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = contentColorFor(color ?: MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Composable
fun CalendarBannerPlaceholder(
    modifier: Modifier = Modifier,
    height: Dp = 120.dp,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        CalendarBannerView(
            imageUrl = null,
            height = height,
            modifier = Modifier.fillMaxWidth(),
            gradientColor = null
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = "15 Jan",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.defaultPlaceholder(visible = true),
                color = contentColorFor(MaterialTheme.colorScheme.outline)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Wednesday",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.defaultPlaceholder(visible = true),
                color = contentColorFor(MaterialTheme.colorScheme.outline)
            )
        }
    }
}

@Preview
@Composable
private fun CalendarBannerPreview() {
    AniHyouTheme {
        Surface {
            CalendarBanner(
                title = "Today",
                date = LocalDateTime.now(),
                height = 120.dp,
                imageUrl = null,
                color = null
            )
        }
    }
}

@Preview
@Composable
private fun CalendarBannerPlaceholderPreview() {
    AniHyouTheme {
        Surface {
            CalendarBannerPlaceholder(
                height = 120.dp,
            )
        }
    }
}
