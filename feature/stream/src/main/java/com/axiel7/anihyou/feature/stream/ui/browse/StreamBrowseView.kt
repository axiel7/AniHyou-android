package com.axiel7.anihyou.feature.stream.ui.browse

import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.axiel7.anihyou.core.model.media.progressOrVolumes
import com.axiel7.anihyou.core.network.type.MediaSeason
import com.axiel7.anihyou.feature.stream.data.model.StreamAnime
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDateTime

@Composable
fun PulsePlaceholder(
    modifier: Modifier,
    shape: Shape = MaterialTheme.shapes.medium
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = alpha * 0.15f))
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamBrowseView(
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StreamBrowseViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var searchActive by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()

    Column(modifier = modifier.fillMaxSize()) {
        // ── Search bar ────────────────────────────────────────────────────────
        SearchBar(
            inputField = {
                androidx.compose.material3.SearchBarDefaults.InputField(
                    query = state.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChanged,
                    onSearch = { searchActive = false },
                    expanded = searchActive,
                    onExpandedChange = { searchActive = it },
                    placeholder = { Text("Search anime…") },
                )
            },
            expanded = searchActive,
            onExpandedChange = { searchActive = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            // Search results inside the expanded search bar
            if (state.isSearching) {
                Box(Modifier.fillMaxWidth().height(120.dp), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                    items(state.searchResults) { anime ->
                        SearchResultRow(anime = anime, onClick = { onAnimeClick(anime.id) })
                    }
                }
            }
        }

        // ── Main content ──────────────────────────────────────────────────────
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadHome() },
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.isLoading && state.spotlight.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Continue watching skeleton
                    item {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            PulsePlaceholder(Modifier.width(130.dp).height(20.dp))
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                repeat(3) {
                                    Column(Modifier.width(110.dp)) {
                                        PulsePlaceholder(Modifier.fillMaxWidth().aspectRatio(2f / 3f))
                                        Spacer(Modifier.height(6.dp))
                                        PulsePlaceholder(Modifier.fillMaxWidth().height(14.dp))
                                    }
                                }
                            }
                        }
                    }
                    // Spotlight skeleton
                    item {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            PulsePlaceholder(Modifier.width(100.dp).height(20.dp))
                            Spacer(Modifier.height(12.dp))
                            PulsePlaceholder(Modifier.fillMaxWidth().height(160.dp), shape = MaterialTheme.shapes.large)
                        }
                    }
                    // Trending skeleton
                    item {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            PulsePlaceholder(Modifier.width(120.dp).height(20.dp))
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                repeat(3) {
                                    Column(Modifier.width(110.dp)) {
                                        PulsePlaceholder(Modifier.fillMaxWidth().aspectRatio(2f / 3f))
                                        Spacer(Modifier.height(6.dp))
                                        PulsePlaceholder(Modifier.fillMaxWidth().height(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // ── Continue Watching (Top Option) ────────────────────────────────
            if (state.currentlyWatching.isNotEmpty() || state.isLoadingCurrentlyWatching) {
                item { SectionHeader("Continue Watching") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        if (state.isLoadingCurrentlyWatching && state.currentlyWatching.isEmpty()) {
                            items(3) {
                                Column(Modifier.width(110.dp)) {
                                    PulsePlaceholder(Modifier.fillMaxWidth().aspectRatio(2f / 3f))
                                    Spacer(Modifier.height(6.dp))
                                    PulsePlaceholder(Modifier.fillMaxWidth().height(14.dp))
                                }
                            }
                        } else {
                            items(state.currentlyWatching) { item ->
                                val media = item.media
                                val title = media?.basicMediaDetails?.title?.userPreferred.orEmpty()
                                val coverUrl = media?.coverImage?.large
                                val progress = item.basicMediaListEntry.progressOrVolumes()

                                ContinueWatchingCard(
                                    title = title,
                                    coverUrl = coverUrl,
                                    progress = progress,
                                    onClick = { onAnimeClick(item.mediaId) }
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            // Spotlight
            if (state.spotlight.isNotEmpty()) {
                item { SectionHeader("Spotlight") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.spotlight) { anime ->
                            SpotlightCard(anime = anime, onClick = { onAnimeClick(anime.id) })
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            // Trending
            if (state.trending.isNotEmpty()) {
                item { SectionHeader("Trending Now") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.trending) { anime ->
                            AnimeCard(anime = anime, onClick = { onAnimeClick(anime.id) })
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            // Popular
            if (state.popular.isNotEmpty()) {
                item { SectionHeader("All-Time Popular") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.popular) { anime ->
                            AnimeCard(anime = anime, onClick = { onAnimeClick(anime.id) })
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            // Recent
            if (state.recent.isNotEmpty()) {
                item { SectionHeader("Currently Airing") }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.recent) { anime ->
                            AnimeCard(anime = anime, onClick = { onAnimeClick(anime.id) })
                        }
                    }
                }
                item { Spacer(Modifier.height(16.dp)) }
            }

            // ── Season Selection ──────────────────────────────────────────────
            item { SectionHeader("Browse by Season") }
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    // Season Filter Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(MediaSeason.WINTER, MediaSeason.SPRING, MediaSeason.SUMMER, MediaSeason.FALL).forEach { s ->
                            val isSelected = state.selectedSeason == s
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onSeasonSelected(s) },
                                label = { Text(s.name.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                    // Year Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val currentYear = LocalDateTime.now().year
                        items((currentYear downTo currentYear - 4).toList()) { y ->
                            val isSelected = state.selectedYear == y
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.onYearSelected(y) },
                                label = { Text(y.toString()) }
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(12.dp)) }

            // Seasonal Anime Row
            if (state.isLoadingSeasonal && state.seasonalAnime.isEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(3) {
                            Column(Modifier.width(110.dp)) {
                                PulsePlaceholder(Modifier.fillMaxWidth().aspectRatio(2f / 3f))
                                Spacer(Modifier.height(6.dp))
                                PulsePlaceholder(Modifier.fillMaxWidth().height(14.dp))
                            }
                        }
                    }
                }
            } else if (state.seasonalAnime.isNotEmpty()) {
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(state.seasonalAnime) { anime ->
                            AnimeCard(anime = anime, onClick = { onAnimeClick(anime.id) })
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "No seasonal anime found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
}
}

@Composable
private fun SectionHeader(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
    ) {
        // Vertical indicator bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(18.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.primary)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SpotlightCard(anime: StreamAnime, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
    ) {
        Box {
            AsyncImage(
                model = anime.bannerImage ?: anime.coverUrl,
                contentDescription = anime.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
            )
            // Premium gradient overlay
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
            // Title & Info container
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
            ) {
                // Spotlight Tag
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraSmall)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "★ Spotlight",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = anime.displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                anime.genres.take(2).joinToString(" • ").takeIf { it.isNotEmpty() }?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeCard(anime: StreamAnime, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = anime.coverUrl,
                contentDescription = anime.displayTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Cover card overlay gradient
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                        )
                    )
            )
            // Badge Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp)
                    .align(Alignment.TopStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Score badge (if present)
                anime.averageScore?.let { score ->
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "★ ${score / 10.0}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                // Format badge (if present)
                anime.format?.let { format ->
                    Box(
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.extraSmall)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.85f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = format,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = anime.displayTitle,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        anime.seasonYear?.let {
            Text(
                text = it.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ContinueWatchingCard(
    title: String,
    coverUrl: String?,
    progress: Int?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(110.dp)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.medium)
        ) {
            AsyncImage(
                model = coverUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            // Progress tag overlay
            if (progress != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(vertical = 4.dp, horizontal = 6.dp)
                ) {
                    Text(
                        text = "Ep $progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SearchResultRow(anime: StreamAnime, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = anime.coverUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(40.dp)
                .aspectRatio(2f / 3f)
                .clip(MaterialTheme.shapes.small),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = anime.displayTitle,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val meta = listOfNotNull(anime.format, anime.seasonYear?.toString()).joinToString(" · ")
            if (meta.isNotEmpty()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        anime.averageScore?.let {
            Text(
                text = "★ ${it / 10.0}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}
