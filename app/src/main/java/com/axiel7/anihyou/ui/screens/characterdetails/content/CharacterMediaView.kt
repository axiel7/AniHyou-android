package com.axiel7.anihyou.ui.screens.characterdetails.content

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.axiel7.anihyou.CharacterMediaQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.character.localized
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder

@Composable
fun CharacterMediaView(
    pagingItems: LazyPagingItems<CharacterMediaQuery.Edge>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (Int) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (pagingItems.loadState.refresh is LoadState.Loading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        }
        items(
            count = pagingItems.itemCount,
            //key = pagingItems.itemKey { it.id!! },
            contentType = { it }
        ) { index ->
            pagingItems[index]?.let { item ->
                MediaItemHorizontal(
                    title = item.node?.title?.userPreferred ?: "",
                    imageUrl = item.node?.coverImage?.large,
                    subtitle1 = {
                        Text(
                            text = item.characterRole?.localized() ?: "",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 15.sp
                        )
                    },
                    subtitle2 = {
                        Text(
                            text = item.voiceActors
                                ?.joinToString { "${it?.name?.userPreferred} (${it?.languageV2})" }
                                ?: "",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 15.sp
                        )
                    },
                    onClick = {
                        navigateToMediaDetails(item.node?.id!!)
                    }
                )
            }
        }
        if (pagingItems.loadState.append is LoadState.Loading) {
            item {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        } else if (pagingItems.itemCount == 0) {
            item {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}