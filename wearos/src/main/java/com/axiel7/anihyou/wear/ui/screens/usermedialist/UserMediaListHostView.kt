package com.axiel7.anihyou.wear.ui.screens.usermedialist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.PageIndicatorState
import com.axiel7.anihyou.core.network.type.MediaType
import com.google.android.horologist.compose.layout.ScreenScaffold

@Composable
fun UserMediaListHostView(
    goToEditMedia: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val maxPages = 2
    var selectedPage by remember { mutableIntStateOf(0) }
    var finalValue by remember { mutableIntStateOf(0) }

    val animatedSelectedPage by animateFloatAsState(
        targetValue = selectedPage.toFloat(),
    ) {
        finalValue = it.toInt()
    }

    val pageIndicatorState: PageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageOffset: Float
                get() = animatedSelectedPage - finalValue
            override val selectedPage: Int
                get() = finalValue
            override val pageCount: Int
                get() = maxPages
        }
    }

    val pagerState = rememberPagerState { maxPages }

    ScreenScaffold(
        modifier = modifier,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.align(Alignment.Center)
            ) { page ->
                if (page !in ((pagerState.currentPage - 1)..(pagerState.currentPage + 1))) {
                    // To make sure only one offscreen page is being composed
                    return@HorizontalPager
                }
                LaunchedEffect(page) { selectedPage = page }

                UserMediaListView(
                    mediaType = if (page == 0) MediaType.ANIME else MediaType.MANGA,
                    goToEditMedia = goToEditMedia,
                )
            }
            HorizontalPageIndicator(
                pageIndicatorState = pageIndicatorState,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}