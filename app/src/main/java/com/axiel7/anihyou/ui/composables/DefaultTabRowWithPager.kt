package com.axiel7.anihyou.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.ui.base.TabRowItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> DefaultTabRowWithPager(
    tabs: Array<TabRowItem<T>>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    isTabScrollable: Boolean = false,
    pageContent: @Composable (Int) -> Unit,
) {
    val state = rememberPagerState(initialPage = initialPage) { tabs.size }
    val scope = rememberCoroutineScope()

    Column(modifier =  modifier) {
        val tabsLayout = @Composable { tabs.forEachIndexed { index, item ->
            Tab(
                selected = state.currentPage == index,
                onClick = { scope.launch { state.animateScrollToPage(index) } },
                text = if (item.title != null) {{
                    Text(text = stringResource(item.title))
                }} else null,
                icon = if (item.icon != null) {{
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.value.toString()
                    )
                }} else null,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }}

        if (isTabScrollable) {
            ScrollableTabRow(
                selectedTabIndex = state.currentPage,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    RoundedTabRowIndicator(tabPositions[state.currentPage])
                },
                tabs = tabsLayout
            )
        } else {
            TabRow(
                selectedTabIndex = state.currentPage,
                indicator = { tabPositions ->
                    RoundedTabRowIndicator(tabPositions[state.currentPage])
                },
                tabs = tabsLayout
            )
        }

        HorizontalPager(
            state = state,
            key = { tabs[it].value!! }
        ) {
            pageContent(it)
        }
    }//: Column
}