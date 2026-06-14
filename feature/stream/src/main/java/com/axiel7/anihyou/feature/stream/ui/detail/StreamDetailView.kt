package com.axiel7.anihyou.feature.stream.ui.detail

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import coil3.compose.AsyncImage
import com.axiel7.anihyou.feature.stream.data.model.AudioType
import com.axiel7.anihyou.feature.stream.data.model.Episode
import org.koin.androidx.compose.koinViewModel

@Composable
private fun DetailPulsePlaceholder(
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StreamDetailView(
    animeId: Int,
    onBack: () -> Unit,
    onPlayEpisode: (animeId: Int, provider: String, category: String, episodeSlug: String, episodeNumber: Int, seasonNumber: Int) -> Unit,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StreamDetailViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedEpisodeForAudioChoice by remember { mutableStateOf<Episode?>(null) }
    var showReminderDialog by remember { mutableStateOf(false) }

    LaunchedEffect(animeId) { viewModel.load(animeId) }

    val currentSeasonNumber = state.seasonsList.indexOfFirst { it.animeId == animeId }.let { if (it == -1) 1 else it + 1 }
    val pullRefreshState = rememberPullToRefreshState()

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
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.load(animeId, isRefresh = true) },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullRefreshState,
                    isRefreshing = state.isLoading,
                    modifier = Modifier.align(Alignment.TopCenter),
                )
            }
        ) {
            if (state.isLoading && state.info == null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        DetailPulsePlaceholder(Modifier.fillMaxWidth().height(180.dp), shape = MaterialTheme.shapes.large)
                    }
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            DetailPulsePlaceholder(Modifier.fillMaxWidth(0.7f).height(24.dp))
                            DetailPulsePlaceholder(Modifier.fillMaxWidth(0.4f).height(16.dp))
                        }
                    }
                    items(5) {
                        DetailPulsePlaceholder(Modifier.fillMaxWidth().height(72.dp), shape = MaterialTheme.shapes.medium)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp),
                ) {
                    item {
                        Box(Modifier.fillMaxWidth().height(220.dp)) {
                            AsyncImage(
                                model = state.info?.bannerImage ?: state.info?.coverUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(
                                                Color.Transparent,
                                                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                                                MaterialTheme.colorScheme.surface
                                            )
                                        )
                                    )
                            )
                        }
                    }

                    state.info?.let { info ->
                        item {
                            Column(Modifier.padding(horizontal = 16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = info.displayTitle,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { showReminderDialog = true }) {
                                        Icon(
                                            painter = painterResource(
                                                if (state.reminderLanguage != null) com.axiel7.anihyou.core.resources.R.drawable.notifications_active_filled_24
                                                else com.axiel7.anihyou.core.resources.R.drawable.notifications_24
                                            ),
                                            contentDescription = "Set Reminder",
                                            tint = if (state.reminderLanguage != null) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(Modifier.height(8.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    info.format?.let { InfoBadge(it, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer) }
                                    info.seasonYear?.let { InfoBadge(it.toString()) }
                                    info.episodes?.let { InfoBadge("$it Episodes") }
                                    info.duration?.let { InfoBadge("${it}m") }
                                }
                                Spacer(Modifier.height(8.dp))

                                info.averageScore?.let { score ->
                                    Text(
                                        text = "★ ${score / 10.0}   •   ${info.popularity?.let { "$it Popularity" } ?: ""}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                }
                                Spacer(Modifier.height(10.dp))

                                if (info.genres.isNotEmpty()) {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        items(info.genres) { genre ->
                                            Box(
                                                modifier = Modifier
                                                    .border(
                                                        width = 1.dp,
                                                        color = MaterialTheme.colorScheme.outlineVariant,
                                                        shape = MaterialTheme.shapes.extraLarge
                                                    )
                                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                                            ) {
                                                Text(
                                                    text = genre,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(12.dp))

                                DetailGrid(info)
                                Spacer(Modifier.height(12.dp))

                                info.description?.let { desc ->
                                    var isDescExpanded by remember { mutableStateOf(false) }
                                    val cleanDesc = desc.replace(Regex("<[^>]*>"), "")
                                    Column {
                                        Text(
                                            text = cleanDesc,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = if (isDescExpanded) Int.MAX_VALUE else 3,
                                            overflow = TextOverflow.Ellipsis,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                        if (cleanDesc.length > 150) {
                                            Text(
                                                text = if (isDescExpanded) "Read less" else "Read more",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .clickable { isDescExpanded = !isDescExpanded }
                                                    .padding(vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

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
                                            currentSeasonNumber,
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Resume Episode $ep")
                            }
                        }
                    }

                    if (state.seasonsList.size > 1) {
                        item {
                            Column(Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = "Seasons",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    itemsIndexed(state.seasonsList) { index, season ->
                                        val isCurrent = season.animeId == animeId
                                        SeasonCard(
                                            seasonNumber = index + 1,
                                            season = season,
                                            onClick = {
                                                if (!isCurrent) {
                                                    onAnimeClick(season.animeId)
                                                }
                                            },
                                            modifier = Modifier.border(
                                                width = if (isCurrent) 2.dp else 0.dp,
                                                color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                shape = MaterialTheme.shapes.medium
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (state.availableProviders.isNotEmpty()) {
                        item {
                            Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Text("Provider", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(6.dp))
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

                    item {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Episodes (${state.episodeList.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )
                            IconButton(onClick = viewModel::toggleSortOrder) {
                                Icon(
                                    painter = painterResource(
                                        if (state.isSortAscending) com.axiel7.anihyou.core.resources.R.drawable.arrow_upward_24
                                        else com.axiel7.anihyou.core.resources.R.drawable.arrow_downward_24
                                    ),
                                    contentDescription = "Toggle Sort Order"
                                )
                            }
                        }
                    }

                    val providerData = state.episodeData?.providers?.get(state.selectedProvider)
                    items(state.episodeList) { episode ->
                        val isWatched = episode.number in state.watchedEpisodes
                        val isResume = episode.number == state.resumeEpisode
                        val hasSub = providerData?.episodes?.sub?.any { it.number == episode.number } == true
                        val hasDub = providerData?.episodes?.dub?.any { it.number == episode.number } == true
                        EpisodeCard(
                            episode = episode,
                            isWatched = isWatched,
                            isResume = isResume,
                            hasSub = hasSub,
                            hasDub = hasDub,
                            onPlay = {
                                if (state.selectedAudio == AudioType.ALL && hasSub && hasDub) {
                                    selectedEpisodeForAudioChoice = episode
                                } else {
                                    val playCategory = if (hasDub && state.selectedAudio == AudioType.DUB) "dub" else "sub"
                                    val playEp = if (playCategory == "dub") {
                                        providerData?.episodes?.dub?.firstOrNull { it.number == episode.number } ?: episode
                                    } else episode
                                    val slug = playEp.id.substringAfterLast("/")
                                    onPlayEpisode(
                                        animeId,
                                        state.selectedProvider,
                                        playCategory,
                                        slug,
                                        episode.number,
                                        currentSeasonNumber,
                                    )
                                }
                            },
                            onToggleWatched = {
                                if (isWatched) viewModel.markUnwatched(episode.number)
                                else viewModel.markWatched(episode.number)
                            },
                            onAddNote = { viewModel.openNoteDialog(episode.number) },
                        )
                    }
                }
            }
        }

        if (selectedEpisodeForAudioChoice != null) {
            val ep = selectedEpisodeForAudioChoice!!
            val providerData = state.episodeData?.providers?.get(state.selectedProvider)
            AlertDialog(
                onDismissRequest = { selectedEpisodeForAudioChoice = null },
                title = { Text("Choose Audio") },
                text = { Text("Select your preferred audio track for streaming:") },
                confirmButton = {
                    Button(
                        onClick = {
                            val slug = ep.id.substringAfterLast("/")
                            onPlayEpisode(animeId, state.selectedProvider, "sub", slug, ep.number, currentSeasonNumber)
                            selectedEpisodeForAudioChoice = null
                        }
                    ) { Text("SUB") }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            val dubEp = providerData?.episodes?.dub?.firstOrNull { it.number == ep.number }
                            val slug = (dubEp ?: ep).id.substringAfterLast("/")
                            onPlayEpisode(animeId, state.selectedProvider, "dub", slug, ep.number, currentSeasonNumber)
                            selectedEpisodeForAudioChoice = null
                        }
                    ) { Text("DUB") }
                }
            )
        }

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

        if (showReminderDialog) {
            AlertDialog(
                onDismissRequest = { showReminderDialog = false },
                title = { Text("Set Episode Drop Reminder") },
                text = { Text("Select your preferred language for episode release notifications:") },
                confirmButton = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.setReminder("sub")
                                showReminderDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SUB Only")
                        }
                        Button(
                            onClick = {
                                viewModel.setReminder("dub")
                                showReminderDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("DUB Only")
                        }
                        Button(
                            onClick = {
                                viewModel.setReminder("both")
                                showReminderDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("BOTH Sub & Dub")
                        }
                        if (state.reminderLanguage != null) {
                            TextButton(
                                onClick = {
                                    viewModel.setReminder(null)
                                    showReminderDialog = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Remove Reminder", color = MaterialTheme.colorScheme.error)
                            }
                        }
                        TextButton(
                            onClick = { showReminderDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cancel")
                        }
                    }
                },
                dismissButton = null
            )
        }
    }
}

@Composable
private fun InfoBadge(
    text: String,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Box(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(containerColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
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
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { (label, value) ->
            Row {
                Text(
                    text = "$label: ",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
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
private fun EpisodeCard(
    episode: Episode,
    isWatched: Boolean,
    isResume: Boolean,
    hasSub: Boolean,
    hasDub: Boolean,
    onPlay: () -> Unit,
    onToggleWatched: () -> Unit,
    onAddNote: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onPlay),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (isResume) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isResume) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
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
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
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
                val displayTitle = if (episode.title.isNullOrBlank()) {
                    "Episode ${episode.number}"
                } else if (episode.title.startsWith("Episode ", ignoreCase = true)) {
                    episode.title
                } else {
                    "${episode.number}. ${episode.title}"
                }
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (hasSub) {
                        Icon(
                            painter = painterResource(com.axiel7.anihyou.core.resources.R.drawable.mic_24),
                            contentDescription = "Sub",
                            tint = Color(0xFFEF5350),
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    if (hasDub) {
                        Icon(
                            painter = painterResource(com.axiel7.anihyou.core.resources.R.drawable.mic_24),
                            contentDescription = "Dub",
                            tint = Color(0xFF66BB6A),
                            modifier = Modifier.size(12.dp)
                        )
                    }
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
            }

            IconButton(onClick = onAddNote, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Add note", modifier = Modifier.size(18.dp))
            }

            IconButton(onClick = onToggleWatched, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = if (isWatched) "Mark unwatched" else "Mark watched",
                    tint = if (isWatched) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun SeasonCard(
    seasonNumber: Int,
    season: SeasonInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(130.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(11f / 16f)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                if (season.coverUrl != null) {
                    AsyncImage(
                        model = season.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Season $seasonNumber",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = season.title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (season.subCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(com.axiel7.anihyou.core.resources.R.drawable.mic_24),
                                contentDescription = "Sub",
                                tint = Color(0xFFEF5350),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = "${season.subCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (season.dubCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(com.axiel7.anihyou.core.resources.R.drawable.mic_24),
                                contentDescription = "Dub",
                                tint = Color(0xFF66BB6A),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = "${season.dubCount}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
