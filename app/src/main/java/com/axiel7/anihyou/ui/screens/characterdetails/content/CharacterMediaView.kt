package com.axiel7.anihyou.ui.screens.characterdetails.content

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.axiel7.anihyou.data.model.character.localized
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetailsViewModel

@Composable
fun CharacterMediaView(
    viewModel: CharacterDetailsViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getCharacterMedia()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = viewModel.characterMedia,
            key = { it.id!! },
            contentType = { it }
        ) { item ->
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
        if (viewModel.isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        } else if (viewModel.characterMedia.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}