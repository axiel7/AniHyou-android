package com.axiel7.anihyou.feature.stream.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.axiel7.anihyou.feature.stream.data.model.AudioType
import com.axiel7.anihyou.feature.stream.data.model.Episode
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamDetailView(
    animeId: Int,
    onBack: () -> Unit,
    onPlayEpisode: (animeId: Int, provider: String, category: String, episodeSlug: String, episodeNumber: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StreamDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(animeId) { viewModel.load(animeId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.info?.displayTitle ?: "Loading…",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        modifier = modifier,
    ) { innerPadding ->
        if (state.isLoading && state.info == null) {
            Box(Modifier.fillMaxSize().padding(innerPadding), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp),
        ) {
            // ── Banner + cover ────────────────────────────────────────────────
            item {
                Box(Modifier.fillMaxWidth().height(200.dp)) {
                    AsyncImage(
                        model = state.info?.bannerImage ?: state.info?.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                    // Gradient overlay
                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                                )
                            )
                    )
                }
            }

            // ── Anime info panel ──────────────────────────────────────────────
            state.info?.let { info ->
                item {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = info.displayTitle,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(4.dp))

                        // Metadata row
                        val meta = buildList {
                            info.format?.let { add(it) }
                            info.seasonYear?.let { add(it.toString()) }
                            info.episodes?.let { add("$it eps") }
                            info.duration?.let { add("${it}m") }
                            info.status?.let { add(it.lowercase().replaceFirstChar { c -> c.uppercase() }) }
                        }.joinToString(" · ")

                        Text(
                            text = meta,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(4.dp))

                        // Score
                        info.averageScore?.let { score ->
                            Text(
                                text = "★ ${score / 10.0} · ${info.popularity?.let { "$it users" } ?: ""}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Spacer(Modifier.height(8.dp))

                        // Genres
                        if (info.genres.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(info.genres) { genre ->
                                    FilterChip(
                                        selected = false,
                                        onClick = {},
                                        label = { Text(genre, style = MaterialTheme.typography.labelSmall) },
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))

                        // Detail grid
                        DetailGrid(info)
                        Spacer(Modifier.height(8.dp))

                        // Description
                        info.description?.let { desc ->
                            Text(
                                text = desc,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 5,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // ── Resume button ─────────────────────────────────────────────────
            state.resumeEpisode?.let { ep ->
                item {
                    Button(
                        onClick = {
                            val episode = state.episodeList.firstOrNull { it.number == ep }
                            if (episode != null) {
                                val slug = episode.id.substringAfterLast("/")
                                onPlayEpisode(
                                    animeId,
                                    state.selectedProvider,
                                    state.selectedAudio.value,
                                    slug,
                                    ep,
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Resume Episode $ep")
                    }
                }
            }

            // ── Provider selector ─────────────────────────────────────────────
            if (state.availableProviders.isNotEmpty()) {
                item {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text("Provider", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        ScrollableTabRow(
                            selectedTabIndex = state.availableProviders.indexOf(state.selectedProvider).coerceAtLeast(0),
                            edgePadding = 0.dp,
                        ) {
                            state.availableProviders.forEach { provider ->
                                Tab(
                                    selected = provider == state.selectedProvider,
                                    onClick = { viewModel.selectProvider(provider) },
                                    text = { Text(provider) },
                                )
                            }
                        }
                    }
                }
            }

            // ── Sub / Dub toggle ──────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    AudioType.entries.forEach { audio ->
                        FilterChip(
                            selected = state.selectedAudio == audio,
                            onClick = { viewModel.selectAudio(audio) },
                            label = { Text(audio.value.uppercase()) },
                        )
                    }
                }
            }

            // ── Episode list header ───────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Episodes (${state.episodeList.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            // ── Episodes ──────────────────────────────────────────────────────
            items(state.episodeList) { episode ->
                val isWatched = episode.number in state.watchedEpisodes
                val isResume = episode.number == state.resumeEpisode
                EpisodeRow(
                    episode = episode,
                    isWatched = isWatched,
                    isResume = isResume,
                    onPlay = {
                        val slug = episode.id.substringAfterLast("/")
                        onPlayEpisode(
                            animeId,
                            state.selectedProvider,
                            state.selectedAudio.value,
                            slug,
                            episode.number,
                        )
                    },
                    onToggleWatched = {
                        if (isWatched) viewModel.markUnwatched(episode.number)
                        else viewModel.markWatched(episode.number)
                    },
                    onAddNote = { viewModel.openNoteDialog(episode.number) },
                )
            }
        }

        // ── Note dialog ───────────────────────────────────────────────────────
        if (state.noteDialogEpisode != null) {
            AlertDialog(
                onDismissRequest = viewModel::dismissNoteDialog,
                title = { Text("Note for Episode ${state.noteDialogEpisode}") },
                text = {
                    OutlinedTextField(
                        value = state.noteDialogText,
                        onValueChange = viewModel::onNoteTextChanged,
                        placeholder = { Text("Add a note…") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                    )
                },
                confirmButton = {
                    Button(onClick = viewModel::saveNote) { Text("Save") }
                },
                dismissButton = {
                    TextButton(onClick = viewModel::dismissNoteDialog) { Text("Cancel") }
                },
            )
        }
    }
}

@Composable
private fun DetailGrid(info: com.axiel7.anihyou.feature.stream.data.model.AnimeInfoResponse) {
    val items = buildList {
        info.mainStudio?.let { add("Studio" to it) }
        info.countryOfOrigin?.let { add("Country" to it) }
        info.source?.let { add("Source" to it.lowercase().replaceFirstChar { c -> c.uppercase() }) }
        info.startDate?.let { date ->
            val str = listOfNotNull(date.year, date.month, date.day).joinToString("-")
            if (str.isNotEmpty()) add("Aired" to str)
        }
    }
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        items.forEach { (label, value) ->
            Row {
                Text(
                    text = "$label: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun EpisodeRow(
    episode: Episode,
    isWatched: Boolean,
    isResume: Boolean,
    onPlay: () -> Unit,
    onToggleWatched: () -> Unit,
    onAddNote: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onPlay)
            .background(
                if (isResume) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail or episode number
        Box(
            modifier = Modifier
                .width(80.dp)
                .aspectRatio(16f / 9f)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (episode.image != null) {
                AsyncImage(
                    model = episode.image,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Text(
                    text = "${episode.number}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            // Watched overlay
            if (isWatched) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Watched",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = episode.title?.takeIf { it.isNotBlank() } ?: "Episode ${episode.number}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isResume) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val meta = buildList {
                episode.durationMinutes?.let { add("${it}m") }
                episode.airDate?.let { add(it) }
                if (episode.filler) add("Filler")
            }.joinToString(" · ")
            if (meta.isNotEmpty()) {
                Text(
                    text = meta,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // Note button
        IconButton(onClick = onAddNote, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Edit, contentDescription = "Add note", modifier = Modifier.size(16.dp))
        }

        // Watch toggle
        IconButton(onClick = onToggleWatched, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = if (isWatched) "Mark unwatched" else "Mark watched",
                tint = if (isWatched) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}
