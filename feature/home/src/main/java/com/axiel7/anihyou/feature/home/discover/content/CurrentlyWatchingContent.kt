package com.axiel7.anihyou.feature.home.discover.content

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.network.fragment.BasicMediaDetails
import com.axiel7.anihyou.core.network.fragment.BasicMediaListEntry
import com.axiel7.anihyou.core.network.fragment.CommonMediaListEntry
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.list.DiscoverLazyRow
import com.axiel7.anihyou.core.ui.composables.list.HorizontalListHeader
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVertical
import com.axiel7.anihyou.core.ui.composables.media.MediaItemVerticalPlaceholder

@Composable
fun CurrentlyWatchingContent(
    currentlyWatching: List<CommonMediaListEntry>,
    isLoading: Boolean,
    onLongClickItem: (BasicMediaDetails, BasicMediaListEntry?) -> Unit,
    navigateToMediaDetails: (mediaId: Int) -> Unit,
) {
    HorizontalListHeader(text = stringResource(R.string.continue_watching))
    DiscoverLazyRow {
        items(
            items = currentlyWatching,
            contentType = { it }
        ) { item ->
            MediaItemVertical(
                title = item.media?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                imageUrl = item.media?.coverImage?.large,
                modifier = Modifier.padding(horizontal = 8.dp),
                subtitle = {
                    val progress = item.basicMediaListEntry.progressOrVolumes()
                    if (progress != null) {
                        Text(
                            text = stringResource(R.string.progress) + ": $progress",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 14.sp,
                        )
                    }
                },
                status = item.basicMediaListEntry.status,
                minLines = 2,
                onClick = { navigateToMediaDetails(item.mediaId) },
                onLongClick = {
                    item.media?.basicMediaDetails?.let { details ->
                        onLongClickItem(details, item.basicMediaListEntry)
                    }
                }
            )
        }
        if (isLoading) {
            items(10) {
                MediaItemVerticalPlaceholder(modifier = Modifier.padding(start = 8.dp))
            }
        }
        if (currentlyWatching.isEmpty() && !isLoading) {
            item { Text(text = stringResource(R.string.no_information)) }
        }
    }
}
