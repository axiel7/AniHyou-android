package com.axiel7.anihyou.ui.screens.home.discover.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_HEIGHT

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverLazyRow(
    minHeight: Dp = MEDIA_POSTER_SMALL_HEIGHT.dp,
    content: LazyListScope.() -> Unit
) {
    val state = rememberLazyListState()
    LazyRow(
        modifier = Modifier
            .sizeIn(minHeight = minHeight),
        state = state,
        contentPadding = PaddingValues(8.dp),
        flingBehavior = rememberSnapFlingBehavior(lazyListState = state),
        content = content
    )
}