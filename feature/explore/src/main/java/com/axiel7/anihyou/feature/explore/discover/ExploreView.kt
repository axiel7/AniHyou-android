package com.axiel7.anihyou.feature.explore.discover

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.axiel7.anihyou.core.model.ExploreTab
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.explore.ExploreSearchBar
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

    LaunchedEffect(selectedTabIndex) {
        viewModel.saveExploreTab(selectedTabIndex)
    }

    Scaffold(
        topBar = {
            ExploreSearchBar(
                isLoggedIn = isLoggedIn
            )
        },
        modifier = modifier,
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

@Preview
@Composable
private fun ExploreViewPreview() {
    AniHyouTheme {
        Surface {
            ExploreView(
                defaultExploreTab = ExploreTab.ANIME,
                isLoggedIn = true,
            )
        }
    }
}
