package com.axiel7.anihyou.feature.mediadetails.watch

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.player.MiruroPlayer

@Composable
fun WatchView(
    anilistId: Int,
    totalEpisodes: Int?,
    userProgress: Int?,
    isDub: Boolean,
    onToggleDub: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val episodeCount = totalEpisodes?.takeIf { it > 0 } ?: 1
    val startEp = (userProgress ?: 0).coerceAtLeast(1).coerceAtMost(episodeCount)

    var selectedEpisode by rememberSaveable { mutableIntStateOf(startEp) }
    var playerVisible by remember { mutableStateOf(true) }

    val listState = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {

        // ── Player ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            if (playerVisible) {
                MiruroPlayer(
                    anilistId = anilistId,
                    episode = selectedEpisode,
                    isDub = isDub,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                CircularProgressIndicator()
            }
        }

        // ── Sub / Dub toggle + episode info ──────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.episode_n, selectedEpisode),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = !isDub,
                    onClick = { onToggleDub(false) },
                    label = { Text("SUB") },
                )
                FilterChip(
                    selected = isDub,
                    onClick = { onToggleDub(true) },
                    label = { Text("DUB") },
                )
            }
        }

        // ── Episode list ─────────────────────────────────────────────────────
        if (episodeCount <= 1 && totalEpisodes == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.no_episodes),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
            ) {
                items(episodeCount) { index ->
                    val epNum = index + 1
                    val isSelected = epNum == selectedEpisode
                    val isWatched = epNum < (userProgress ?: 0)

                    EpisodeListItem(
                        episodeNumber = epNum,
                        isSelected = isSelected,
                        isWatched = isWatched,
                        onClick = {
                            selectedEpisode = epNum
                            playerVisible = false
                            playerVisible = true
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeListItem(
    episodeNumber: Int,
    isSelected: Boolean,
    isWatched: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isWatched  -> MaterialTheme.colorScheme.surfaceVariant
        else       -> MaterialTheme.colorScheme.surface
    }
    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isWatched  -> MaterialTheme.colorScheme.onSurfaceVariant
        else       -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(bgColor)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.episode_n, episodeNumber),
            color = textColor,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        )
        if (isWatched) {
            Text(
                text = "✓",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
            )
        }
    }
}
