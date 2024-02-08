package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.ui.common.TabRowItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> TabRowWithPager(
    tabs: Array<TabRowItem<T>>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    isTabScrollable: Boolean = false,
    isPrimaryTab: Boolean = true,
    pageContent: @Composable (Int) -> Unit,
) {
    val state = rememberPagerState(initialPage = initialPage) { tabs.size }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        val tabsLayout = @Composable {
            tabs.forEachIndexed { index, item ->
                Tab(
                    selected = state.currentPage == index,
                    onClick = { scope.launch { state.animateScrollToPage(index) } },
                    text = if (item.title != null) {
                        {
                            Text(text = stringResource(item.title))
                        }
                    } else null,
                    icon = if (item.icon != null) {
                        {
                            Icon(
                                painter = painterResource(item.icon),
                                contentDescription = item.value.toString()
                            )
                        }
                    } else null,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (isTabScrollable) {
            if (isPrimaryTab) {
                ScrollableTabRow(
                    selectedTabIndex = state.currentPage,
                    edgePadding = 16.dp,
                    indicator = {
                        if (state.currentPage < it.size) {
                            TabRowDefaults.PrimaryIndicator(
                                Modifier.tabIndicatorOffset(it[state.currentPage])
                            )
                        }
                    },
                    tabs = tabsLayout
                )
            } else {
                SecondaryScrollableTabRow(
                    selectedTabIndex = state.currentPage,
                    edgePadding = 16.dp,
                    tabs = tabsLayout
                )
            }
        } else {
            if (isPrimaryTab) {
                TabRow(
                    selectedTabIndex = state.currentPage,
                    indicator = {
                        if (state.currentPage < it.size) {
                            TabRowDefaults.PrimaryIndicator(
                                Modifier.tabIndicatorOffset(it[state.currentPage])
                            )
                        }
                    },
                    tabs = tabsLayout
                )
            } else {
                SecondaryTabRow(
                    selectedTabIndex = state.currentPage,
                    tabs = tabsLayout
                )
            }
        }

        HorizontalPager(
            state = state,
            key = { tabs[it].value }
        ) { page ->
            if (page !in ((state.currentPage - 1)..(state.currentPage + 1))) {
                // To make sure only one offscreen page is being composed
                return@HorizontalPager
            }
            pageContent(page)
        }
    }//: Column
}