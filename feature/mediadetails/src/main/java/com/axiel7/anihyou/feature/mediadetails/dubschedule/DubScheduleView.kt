package com.axiel7.anihyou.feature.mediadetails.dubschedule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.common.utils.CalendarUtils.insertAiringEvent
import com.axiel7.anihyou.core.common.utils.CalendarUtils.openCalendarOnDate
import com.axiel7.anihyou.core.network.api.TvdbEpisode
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.player.VidFastMediaType
import com.axiel7.anihyou.core.ui.composables.player.VidFastPlayer
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Dub schedule tab shown inside MediaDetailsView.
 *
 * Displays:
 *  - Episode list with English dub air dates from TheTVDB
 *  - "Add to Calendar" button per episode (Google Calendar intent)
 *  - "Watch" button per episode (VidFast player in a bottom sheet)
 *  - "View in Calendar" button on the date header
 *
 * @param mediaTitle     English or Romaji title to search TheTVDB
 * @param romajiTitle    Fallback Romaji title
 * @param tmdbId         TMDB TV show ID — used for VidFast embed URL
 * @param seasonNumber   Season to show (default 1)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DubScheduleView(
    mediaTitle: String?,
    romajiTitle: String?,
    tmdbId: String?,
    seasonNumber: Int = 1,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val viewModel: DubScheduleViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Player state
    var playerEpisode by remember { mutableStateOf<Int?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(mediaTitle, romajiTitle) {
        viewModel.loadDubSchedule(mediaTitle, romajiTitle, seasonNumber)
    }

    // VidFast player bottom sheet
    if (playerEpisode != null && tmdbId != null) {
        ModalBottomSheet(
            onDismissRequest = { playerEpisode = null },
            sheetState = sheetState,
        ) {
            Column(modifier = Modifier.padding(bottom = 32.dp)) {
                Text(
                    text = "Episode $playerEpisode",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
                Text(
                    text = "⚠ Personal use only — streams sourced from VidFast",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                VidFastPlayer(
                    mediaId = tmdbId,
                    mediaType = VidFastMediaType.TV,
                    season = seasonNumber,
                    episode = playerEpisode!!,
                    showNextButton = true,
                    autoNext = false,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    when {
        uiState.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.tvdbNotFound -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Dub schedule not found on TheTVDB",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = {
                        viewModel.loadDubSchedule(mediaTitle, romajiTitle, seasonNumber)
                    }) {
                        Text("Retry")
                    }
                }
            }
        }

        uiState.dubEpisodes.isNotEmpty() -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    uiState.tvdbSeries?.let { series ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = series.name ?: "Unknown",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                )
                                Text(
                                    text = "English dub schedule — Season $seasonNumber",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Text(
                                text = "via TheTVDB",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        HorizontalDivider()
                    }
                }

                items(
                    items = uiState.dubEpisodes,
                    key = { it.id },
                ) { episode ->
                    DubEpisodeItem(
                        episode = episode,
                        onAddToCalendar = {
                            episode.aired?.let { dateStr ->
                                val epochSeconds = runCatching {
                                    LocalDate.parse(dateStr)
                                        .atTime(20, 0)
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toEpochSecond()
                                }.getOrNull()
                                if (epochSeconds != null) {
                                    context.insertAiringEvent(
                                        title = "${uiState.tvdbSeries?.name} S${seasonNumber}E${episode.number} (Dub)",
                                        startEpochSeconds = epochSeconds,
                                        description = episode.overview ?: "",
                                    )
                                }
                            }
                        },
                        onViewInCalendar = {
                            episode.aired?.let { context.openCalendarOnDate(it) }
                        },
                        onWatch = if (tmdbId != null) {
                            { playerEpisode = episode.number }
                        } else null,
                    )
                }
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize().padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No dub episodes available yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun DubEpisodeItem(
    episode: TvdbEpisode,
    onAddToCalendar: () -> Unit,
    onViewInCalendar: () -> Unit,
    onWatch: (() -> Unit)?,
) {
    val formattedDate = remember(episode.aired) {
        episode.aired?.let {
            runCatching {
                LocalDate.parse(it)
                    .format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            }.getOrElse { episode.aired }
        }
    }

    val hasAired = remember(episode.aired) {
        episode.aired?.let {
            runCatching { LocalDate.parse(it).isBefore(LocalDate.now().plusDays(1)) }.getOrElse { false }
        } ?: false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Episode number badge
            Surface(
                shape = MaterialTheme.shapes.small,
                color = if (hasAired)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(40.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "${episode.number}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasAired)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.name ?: "Episode ${episode.number}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (formattedDate != null) {
                        if (hasAired) "Aired: $formattedDate" else "Dub: $formattedDate"
                    } else {
                        "No dub date yet"
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (hasAired)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Action icons
            Row {
                if (formattedDate != null) {
                    // Add to Google Calendar
                    IconButton(onClick = onAddToCalendar) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_month_24),
                            contentDescription = "Add to Calendar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    // Open calendar on that date
                    IconButton(onClick = onViewInCalendar) {
                        Icon(
                            painter = painterResource(R.drawable.calendar_today_24),
                            contentDescription = "View in Calendar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                // Watch — only if TMDB ID is available and episode has aired
                if (onWatch != null && hasAired) {
                    IconButton(onClick = onWatch) {
                        Icon(
                            painter = painterResource(R.drawable.play_circle_24),
                            contentDescription = "Watch",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }

        // Episode overview
        episode.overview?.takeIf { it.isNotBlank() }?.let { overview ->
            var expanded by remember { mutableStateOf(false) }
            Text(
                text = overview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = if (expanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp)
                    .clickable { expanded = !expanded },
            )
        }
    }
}
