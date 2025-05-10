package com.axiel7.anihyou.feature.staffdetails.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.network.StaffCharacterQuery
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.common.TriFilterChip
import com.axiel7.anihyou.core.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.core.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontal

@Composable
fun StaffCharacterView(
    staffCharacters: List<StaffCharacterQuery.Edge>,
    isLoading: Boolean,
    loadMore: () -> Unit,
    charactersOnMyList: Boolean?,
    setCharactersOnMyList: (Boolean?) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToCharacterDetails: (Int) -> Unit,
) {
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
            Box(modifier = Modifier.fillMaxWidth()) {
                TriFilterChip(
                    text = stringResource(R.string.on_my_list),
                    value = charactersOnMyList,
                    onValueChanged = setCharactersOnMyList,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
        items(
            items = staffCharacters,
            contentType = { it }
        ) { item ->
            item.characters?.forEach { character ->
                PersonItemHorizontal(
                    title = character?.name?.userPreferred.orEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    imageUrl = character?.image?.large,
                    subtitle = item.node?.title?.userPreferred.orEmpty(),
                    onClick = {
                        navigateToCharacterDetails(character!!.id)
                    }
                )
            }
        }
        if (isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        } else if (staffCharacters.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}