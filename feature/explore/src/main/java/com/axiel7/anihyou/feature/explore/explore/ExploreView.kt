package com.axiel7.anihyou.feature.explore.explore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.semantics.testTagsAsResourceId
import com.axiel7.anihyou.core.model.ExploreTab
import com.axiel7.anihyou.feature.explore.anime.AnimeDiscoverView
import com.axiel7.anihyou.feature.explore.manga.MangaDiscoverView
import com.axiel7.anihyou.feature.explore.recommendations.RecommendationsView
import org.koin.compose.viewmodel.koinActivityViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExploreView(
    defaultExploreTab: ExploreTab,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(defaultExploreTab.ordinal) }
    val viewModel: ExploreViewModel = koinActivityViewModel()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(selectedTabIndex) {
        viewModel.saveExploreTab(selectedTabIndex)
    }

    Scaffold(
        topBar = {
            ExploreSearchBar(
                isLoggedIn = isLoggedIn,
                selectedTabIndex = selectedTabIndex,
                scrollBehavior = scrollBehavior,
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(
                        with(LocalDensity.current) {
                            WindowInsets.statusBars.getTop(this).toDp()
                        }
                    )
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                ExploreTab.entries.forEach { tab ->
                    Tab(
                        selected = selectedTabIndex == tab.ordinal,
                        onClick = { selectedTabIndex = tab.ordinal },
                        modifier = Modifier.semantics {
                            testTagsAsResourceId = true
                            testTag = tab.name
                        },
                        text = { Text(text = tab.localized()) }
                    )
                }
            }

            when (ExploreTab.entries[selectedTabIndex]) {
                ExploreTab.ANIME -> {
                    AnimeDiscoverView(
                        isLoggedIn = isLoggedIn,
                        contentPadding = contentPadding
                    )
                }

                ExploreTab.MANGA -> {
                    MangaDiscoverView(
                        isLoggedIn = isLoggedIn,
                        contentPadding = contentPadding
                    )
                }

                ExploreTab.RECOMMENDATIONS -> {
                    RecommendationsView(
                        isLoggedIn = isLoggedIn,
                        contentPadding = contentPadding
                    )
                }
            }
        }
    }
}
