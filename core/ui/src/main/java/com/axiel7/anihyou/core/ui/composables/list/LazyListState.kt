package com.axiel7.anihyou.core.ui.composables.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Extension function to load more items when the bottom is reached
 * @param buffer Tells how many items before it reaches the bottom of the list to call `onLoadMore`. This value should be >= 0
 * @param onLoadMore The code to execute when it reaches the bottom of the list
 * @author Manav Tamboli
 */
@OptIn(FlowPreview::class)
@Composable
fun LazyListState.OnBottomReached(
    buffer: Int,
    debounceDuration: Duration = 0.seconds,
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
            .debounce(debounceDuration)
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

@Composable
fun LazyListState.rememberIsScrollingUp(): State<Boolean> {
    var lastListIndex by remember { mutableIntStateOf(firstVisibleItemIndex) }
    var lastListOffset by remember { mutableIntStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            val listAtTop = firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0

            if (listAtTop) {
                lastListIndex = 0
                lastListOffset = 0
                return@derivedStateOf false
            } else {
                val listScrollingUp = if (firstVisibleItemIndex != lastListIndex) {
                    firstVisibleItemIndex < lastListIndex
                } else {
                    firstVisibleItemScrollOffset < lastListOffset
                }.also {
                    lastListIndex = firstVisibleItemIndex
                    lastListOffset = firstVisibleItemScrollOffset
                }

                 return@derivedStateOf listScrollingUp
            }
        }
    }
}

@Composable
fun LazyGridState.rememberIsScrollingUp(): State<Boolean> {
    var lastListIndex by remember { mutableIntStateOf(firstVisibleItemIndex) }
    var lastListOffset by remember { mutableIntStateOf(firstVisibleItemScrollOffset) }

    return remember(this) {
        derivedStateOf {
            val listAtTop = firstVisibleItemIndex == 0 && firstVisibleItemScrollOffset == 0

            if (listAtTop) {
                lastListIndex = 0
                lastListOffset = 0
                return@derivedStateOf false
            } else {
                val listScrollingUp = if (firstVisibleItemIndex != lastListIndex) {
                    firstVisibleItemIndex < lastListIndex
                } else {
                    firstVisibleItemScrollOffset < lastListOffset
                }.also {
                    lastListIndex = firstVisibleItemIndex
                    lastListOffset = firstVisibleItemScrollOffset
                }

                return@derivedStateOf listScrollingUp
            }
        }
    }
}

@Composable
fun rememberIsScrollingUp(
    listState: LazyListState,
    gridState: LazyGridState,
): State<Boolean> {
    var lastListIndex by remember { mutableIntStateOf(listState.firstVisibleItemIndex) }
    var lastListOffset by remember { mutableIntStateOf(listState.firstVisibleItemScrollOffset) }
    var lastGridIndex by remember { mutableIntStateOf(gridState.firstVisibleItemIndex) }
    var lastGridOffset by remember { mutableIntStateOf(gridState.firstVisibleItemScrollOffset) }

    return remember(listState, gridState) {
        derivedStateOf {
            val listAtTop =
                listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0
            val gridAtTop =
                gridState.firstVisibleItemIndex == 0 && gridState.firstVisibleItemScrollOffset == 0

            if (listAtTop && gridAtTop) {
                lastListIndex = 0
                lastListOffset = 0
                lastGridIndex = 0
                lastGridOffset = 0
                false
            } else {
                val listScrollingUp = if (listState.firstVisibleItemIndex != lastListIndex) {
                    listState.firstVisibleItemIndex < lastListIndex
                } else {
                    listState.firstVisibleItemScrollOffset < lastListOffset
                }.also {
                    lastListIndex = listState.firstVisibleItemIndex
                    lastListOffset = listState.firstVisibleItemScrollOffset
                }

                val gridScrollingUp = if (gridState.firstVisibleItemIndex != lastGridIndex) {
                    gridState.firstVisibleItemIndex < lastGridIndex
                } else {
                    gridState.firstVisibleItemScrollOffset < lastGridOffset
                }.also {
                    lastGridIndex = gridState.firstVisibleItemIndex
                    lastGridOffset = gridState.firstVisibleItemScrollOffset
                }

                listScrollingUp || gridScrollingUp
            }
        }
    }
}