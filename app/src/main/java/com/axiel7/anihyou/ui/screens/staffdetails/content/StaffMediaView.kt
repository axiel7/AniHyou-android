package com.axiel7.anihyou.ui.screens.staffdetails.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.staff.StaffMediaGrouped
import com.axiel7.anihyou.ui.composables.OnMyListChip
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder

@Composable
fun StaffMediaView(
    staffMedia: List<Pair<Int, StaffMediaGrouped>>,
    isLoading: Boolean,
    loadMore: () -> Unit,
    mediaOnMyList: Boolean?,
    setMediaOnMyList: (Boolean?) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (Int) -> Unit,
) {
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3, onLoadMore = loadMore)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth()) {
                OnMyListChip(
                    onMyList = mediaOnMyList,
                    onMyListChanged = setMediaOnMyList,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }
        }
        items(
            items = staffMedia,
            key = { it.second.value.id!! },
            contentType = { it.second }
        ) { item ->
            MediaItemHorizontal(
                title = item.second.value.node?.title?.userPreferred.orEmpty(),
                imageUrl = item.second.value.node?.coverImage?.large,
                subtitle1 = {
                    Text(
                        text = item.second.staffRoles.joinToString(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                },
                onClick = {
                    navigateToMediaDetails(item.first)
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