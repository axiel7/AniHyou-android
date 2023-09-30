package com.axiel7.anihyou.ui.screens.home.discover.content

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.App
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.PreferencesDataStore
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.ui.composables.list.HorizontalListHeader
import com.axiel7.anihyou.ui.composables.media.AiringAnimeHorizontalItem
import com.axiel7.anihyou.ui.composables.media.AiringAnimeHorizontalItemPlaceholder
import com.axiel7.anihyou.ui.composables.media.MEDIA_POSTER_SMALL_HEIGHT
import com.axiel7.anihyou.ui.screens.home.discover.DiscoverViewModel
import com.axiel7.anihyou.ui.screens.home.discover.composables.DiscoverLazyRow
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.UNKNOWN_CHAR

@Composable
fun AiringContent(
    viewModel: DiscoverViewModel,
    navigateToCalendar: () -> Unit,
    navigateToMediaDetails: (mediaId: Int) -> Unit
) {
    val airingOnMyList by PreferencesDataStore.rememberPreference(
        PreferencesDataStore.AIRING_ON_MY_LIST_PREFERENCE_KEY,
        App.airingOnMyList
    )

    HorizontalListHeader(
        text = stringResource(R.string.airing),
        onClick = navigateToCalendar
    )
    if (airingOnMyList == true) {
        val airingAnime by viewModel.airingAnimeOnMyList.collectAsState()
        DiscoverLazyRow(
            minHeight = MEDIA_POSTER_SMALL_HEIGHT.dp
        ) {
            when (airingAnime) {
                is PagedResult.Loading -> {
                    items(10) {
                        AiringAnimeHorizontalItemPlaceholder()
                    }
                }

                is PagedResult.Success -> {
                    items(
                        items = (airingAnime as PagedResult.Success).data,
                        contentType = { it }
                    ) { item ->
                        AiringAnimeHorizontalItem(
                            title = item.title?.userPreferred ?: "",
                            subtitle = stringResource(
                                R.string.airing_in,
                                item.nextAiringEpisode?.timeUntilAiring?.toLong()
                                    ?.secondsToLegibleText() ?: UNKNOWN_CHAR
                            ),
                            imageUrl = item.coverImage?.large,
                            score = if (item.meanScore != null) "${item.meanScore}%" else null,
                            onClick = {
                                navigateToMediaDetails(item.id)
                            }
                        )
                    }
                }

                is PagedResult.Error -> {
                    item {
                        Text(text = (airingAnime as PagedResult.Error).message)
                    }
                }
            }
        }//:LazyRow
    } else if (airingOnMyList == false) {
        val airingAnimeState by viewModel.airingAnime.collectAsState()
        DiscoverLazyRow(
            minHeight = MEDIA_POSTER_SMALL_HEIGHT.dp
        ) {
            when (airingAnimeState) {
                is PagedResult.Loading -> {
                    items(10) {
                        AiringAnimeHorizontalItemPlaceholder()
                    }
                }

                is PagedResult.Success -> {
                    items(
                        items = (airingAnimeState as PagedResult.Success).data,
                        contentType = { it }
                    ) { item ->
                        AiringAnimeHorizontalItem(
                            title = item.media?.title?.userPreferred ?: "",
                            subtitle = stringResource(
                                R.string.airing_in,
                                item.timeUntilAiring.toLong().secondsToLegibleText()
                            ),
                            imageUrl = item.media?.coverImage?.large,
                            score = if (item.media?.meanScore != null) "${item.media.meanScore}%" else null,
                            onClick = {
                                navigateToMediaDetails(item.media!!.id)
                            }
                        )
                    }
                }

                is PagedResult.Error -> {
                    item {
                        Text(text = (airingAnimeState as PagedResult.Error).message)
                    }
                }
            }
        }//:LazyRow
    }
}