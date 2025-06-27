package com.axiel7.anihyou.feature.home.current.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.common.utils.NumberUtils.isGreaterThanZero
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.exampleCommonMediaListEntry
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.ScoreFormat
import com.axiel7.anihyou.core.ui.composables.IncrementOneButton
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.media.AiringScheduleText
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_COMPACT_HEIGHT
import com.axiel7.anihyou.core.ui.composables.media.MEDIA_POSTER_COMPACT_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.MediaPoster
import com.axiel7.anihyou.core.ui.composables.scores.BadgeScoreIndicator
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrentListItem(
    modifier: Modifier = Modifier,
    item: CommonMediaListEntry,
    scoreFormat: ScoreFormat,
    isPlusEnabled: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onClickPlus: (Int) -> Unit,
    blockPlus: () -> Unit,
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Max)
            .padding(start = 16.dp, end = 0.dp, top = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(onLongClick = onLongClick, onClick = onClick),
    ) {
        Box {
            MediaPoster(
                url = item.media?.coverImage?.large,
                showShadow = false,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .size(
                        width = MEDIA_POSTER_COMPACT_WIDTH.dp,
                        height = MEDIA_POSTER_COMPACT_HEIGHT.dp
                    )
            )

            if (item.basicMediaListEntry.score?.isGreaterThanZero() == true) {
                BadgeScoreIndicator(
                    modifier = Modifier.align(Alignment.BottomStart),
                    score = item.basicMediaListEntry.score,
                    scoreFormat = scoreFormat
                )
            }
        }//: Box

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 16.sp,
                lineHeight = 19.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            AiringScheduleText(
                item = item,
                fontSize = 16.sp,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val progress = item.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
                val duration = item.duration()?.format()
                Text(
                    text = if (duration != null) "$progress/$duration" else "$progress",
                    fontSize = 15.sp,
                )

                IncrementOneButton(
                    onClickPlus = onClickPlus,
                    blockPlus = blockPlus,
                    enabled = isPlusEnabled,
                )
            }//:Row
        }//:Column
    }//:Row
}

@Composable
fun CurrentListItemPlaceholder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .size(
                width = 350.dp,
                height = (MEDIA_POSTER_COMPACT_HEIGHT + 16).dp
            )
            .padding(start = 16.dp, end = 0.dp, top = 8.dp, bottom = 8.dp),
    ) {
        Box(
            modifier = Modifier
                .size(
                    width = MEDIA_POSTER_COMPACT_WIDTH.dp,
                    height = MEDIA_POSTER_COMPACT_HEIGHT.dp
                )
                .defaultPlaceholder(visible = true)
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "This is a placeholder",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 16.sp,
            )
            Text(
                text = "Loading item",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 15.sp,
            )

            Text(
                text = "Loading",
                modifier = Modifier.defaultPlaceholder(visible = true),
                fontSize = 15.sp,
            )
        }//:Column
    }//:Row
}

@Preview
@Composable
fun CurrentListItemPreview() {
    AniHyouTheme {
        Surface {
            Column {
                CurrentListItem(
                    item = exampleCommonMediaListEntry,
                    scoreFormat = ScoreFormat.POINT_10_DECIMAL,
                    isPlusEnabled = true,
                    onClick = {},
                    onLongClick = {},
                    onClickPlus = {},
                    blockPlus = {},
                )
                CurrentListItemPlaceholder()
            }
        }
    }
}