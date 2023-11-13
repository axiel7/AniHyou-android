package com.axiel7.anihyou.ui.screens.characterdetails.content

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.character.localized
import com.axiel7.anihyou.data.model.media.icon
import com.axiel7.anihyou.data.model.media.localized
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder

@Composable
fun CharacterMediaView(
    media: List<CharacterMediaQuery.Edge>,
    isLoading: Boolean,
    loadMore: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (Int) -> Unit,
    showVoiceActorsSheet: (CharacterMediaQuery.Edge) -> Unit,
    showEditSheet: (CharacterMediaQuery.Edge) -> Unit,
) {
    val listState = rememberLazyListState()
    listState.OnBottomReached(buffer = 3, onLoadMore = loadMore)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = media,
            contentType = { it }
        ) { item ->
            MediaItemHorizontal(
                title = item.node?.basicMediaDetails?.title?.userPreferred.orEmpty(),
                imageUrl = item.node?.coverImage?.large,
                subtitle1 = {
                    Text(
                        text = item.characterRole?.localized().orEmpty(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                },
                subtitle2 = {
                    if (!item.voiceActors.isNullOrEmpty()) {
                        TextButton(onClick = { showVoiceActorsSheet(item) }) {
                            Icon(
                                painter = painterResource(R.drawable.record_voice_over_24),
                                contentDescription = stringResource(R.string.voice_actors),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(text = stringResource(R.string.voice_actors))
                        }
                    }
                },
                badgeContent = item.node?.mediaListEntry?.basicMediaListEntry?.status?.let { status ->
                    {
                        Icon(
                            painter = painterResource(status.icon()),
                            contentDescription = status.localized()
                        )
                    }
                },
                onClick = {
                    navigateToMediaDetails(item.node?.id!!)
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
        } else if (media.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}