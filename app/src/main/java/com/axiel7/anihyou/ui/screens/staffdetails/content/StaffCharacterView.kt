package com.axiel7.anihyou.ui.screens.staffdetails.content

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
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetailsViewModel

@Composable
fun StaffCharacterView(
    viewModel: StaffDetailsViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToCharacterDetails: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPageCharacter) viewModel.getStaffCharacters()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = viewModel.staffCharacters,
            key = { it.id!! },
            contentType = { it }
        ) { item ->
            item.characters?.forEach { character ->
                PersonItemHorizontal(
                    title = character?.name?.userPreferred ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    imageUrl = character?.image?.large,
                    subtitle = item.node?.title?.userPreferred ?: "",
                    onClick = {
                        navigateToCharacterDetails(character!!.id)
                    }
                )
            }
        }
        if (viewModel.isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        } else if (viewModel.staffCharacters.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}