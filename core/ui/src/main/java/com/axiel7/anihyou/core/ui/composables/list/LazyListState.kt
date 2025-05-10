package com.axiel7.anihyou.core.ui.composables.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

/**
 * Extension function to load more items when the bottom is reached
 * @param buffer Tells how many items before it reaches the bottom of the list to call `onLoadMore`. This value should be >= 0
 * @param onLoadMore The code to execute when it reaches the bottom of the list
 * @author Manav Tamboli
 */
@Composable
fun LazyListState.OnBottomReached(
    buffer: Int,
    onLoadMore: suspend () -> Unit
) {
    // Buffer must be positive.
    // Or our list will never reach the bottom.
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            // subtract buffer from the total items
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) onLoadMore() }
    }
}

/**
 * Extension function to load more items when the bottom is reached
 * @param buffer Tells how many items before it reaches the bottom of the list to call `onLoadMore`. This value should be >= 0
 * @param onLoadMore The code to execute when it reaches the bottom of the list
 * @author Manav Tamboli
 */
@Composable
fun LazyGridState.OnBottomReached(
    buffer: Int,
    onLoadMore: suspend () -> Unit
) {
    // Buffer must be positive.
    // Or our list will never reach the bottom.
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }

    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true

            // subtract buffer from the total items
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) onLoadMore() }
    }
}