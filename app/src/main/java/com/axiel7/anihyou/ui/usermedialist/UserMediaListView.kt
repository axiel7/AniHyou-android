package com.axiel7.anihyou.ui.usermedialist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun UserMediaListView() {
    val viewModel: UserMediaListViewModel = viewModel()
    val listState = rememberLazyListState()

    Scaffold { padding ->
        Column(
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                state = listState
            ) {
                items(viewModel.mediaList) {
                    Text(text = it.media?.title?.userPreferred ?: "")
                }
            }
        }
    }

    listState.OnBottomReached(buffer = 3) {
        viewModel.getUserList()
    }
}

@Preview
@Composable
fun UserMediaListViewPreview() {
    AniHyouTheme {
        UserMediaListView()
    }
}