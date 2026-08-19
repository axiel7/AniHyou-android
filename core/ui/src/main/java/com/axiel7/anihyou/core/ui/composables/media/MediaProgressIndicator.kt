package com.axiel7.anihyou.core.ui.composables.media

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.common.utils.NumberUtils.format
import com.axiel7.anihyou.core.common.utils.StringUtils.orUnknown
import com.axiel7.anihyou.core.model.media.duration
import com.axiel7.anihyou.core.model.media.isUsingVolumeProgress
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.network.type.MediaStatus
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.ui.utils.ComposeDateUtils.minutesToLegibleText
import com.axiel7.anihyou.core.resources.R


@Composable
fun MediaProgressIndicator(
    item: CommonMediaListEntry,
    singleEpisode: Boolean,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 16.sp,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(4.dp)
) {
    val progress = item.basicMediaListEntry.progressOrVolumes()?.format() ?: 0
    val duration = item.duration()?.format()
    val isNotYetReleased = item.media?.status == MediaStatus.NOT_YET_RELEASED
    val episodeDuration = item.media?.basicMediaDetails?.duration

    val progressText = when {
        isNotYetReleased -> null
        singleEpisode -> episodeDuration?.toLong()?.minutesToLegibleText()
        duration != null -> "$progress/$duration"
        else -> "$progress"
    }.orUnknown()

    val (iconRes, contentDescRes) = when {
        item.basicMediaListEntry.isUsingVolumeProgress() ->
            R.drawable.bookmark_20 to R.string.volumes
        item.media?.basicMediaDetails?.type == MediaType.MANGA ->
            R.drawable.book_20 to R.string.chapters
        singleEpisode ->
            R.drawable.movie_20 to R.string.episodes
        else ->
            R.drawable.play_arrow_24 to R.string.episodes
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = stringResource(contentDescRes)
        )
        Text(
            text = progressText,
            fontSize = fontSize,
            maxLines = 1
        )
    }
}