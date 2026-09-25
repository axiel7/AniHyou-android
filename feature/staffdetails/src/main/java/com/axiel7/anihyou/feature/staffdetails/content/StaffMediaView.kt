package com.axiel7.anihyou.feature.staffdetails.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.model.media.localized
import com.axiel7.anihyou.core.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.core.network.type.MediaType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.LocalBlurAdult
import com.axiel7.anihyou.core.ui.composables.common.FilterSelectionChip
import com.axiel7.anihyou.core.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder

@Composable
fun StaffMediaView(
    staffMedia: SnapshotStateList<Pair<Int, StaffMediaGrouped>>,
    isLoading: Boolean,
    loadMore: () -> Unit,
    mediaOnMyList: Boolean?,
    setMediaOnMyList: (Boolean?) -> Unit,
    mediaType: MediaType?,
    setMediaType: (MediaType?) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    showEditSheet: (Pair<Int, StaffMediaGrouped>) -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val blurAdult = LocalBlurAdult.current
    val listState = rememberLazyListState()
    if (!isLoading) {
        listState.OnBottomReached(buffer = 3, onLoadMore = loadMore)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TriFilterChip(
                    text = stringResource(R.string.on_my_list),
                    value = mediaOnMyList,
                    onValueChanged = setMediaOnMyList,
                )
                MediaType.knownEntries.forEach { type ->
                    FilterSelectionChip(
                        selected = type == mediaType,
                        onClick = { setMediaType(type.takeIf { it != mediaType }) },
                        text = type.localized(),
                    )
                }
            }
        }
        items(
            items = staffMedia,
            contentType = { it.second }
        ) { item ->
            MediaItemHorizontal(
                title = item.second.value.node?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                imageUrl = item.second.value.node?.coverImage?.large,
                blurImage = blurAdult && item.second.value.node?.basicMediaDetails?.isAdult == true,
                subtitle1 = {
                    Text(
                        text = item.second.staffRoles.joinToString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                },
                status = item.second.value.node?.mediaListEntry?.basicMediaListEntry?.status,
                onClick = {
                    navigateToMediaDetails(item.first)
                },
                onLongClick = {
                    showEditSheet(item)
                }
            )
        }
        if (isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        } else if (staffMedia.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}