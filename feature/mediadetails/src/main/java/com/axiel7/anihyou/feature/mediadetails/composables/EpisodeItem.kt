package com.axiel7.anihyou.feature.mediadetails.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.ui.composables.media.VIDEO_SMALL_WIDTH
import com.axiel7.anihyou.core.ui.composables.media.VideoThumbnailItem

@Composable
fun EpisodeItem(
    item: MediaDetailsQuery.StreamingEpisode?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(modifier = modifier) {
        VideoThumbnailItem(
            imageUrl = item?.thumbnail,
            modifier = Modifier.padding(8.dp),
            onClick = onClick
        )
        Text(
            text = item?.title.orEmpty(),
            modifier = Modifier
                .width(VIDEO_SMALL_WIDTH.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 15.sp,
            lineHeight = 18.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}