package com.axiel7.anihyou.feature.mediadetails.watch

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.axiel7.anihyou.core.base.MIRURO_BASE_URL
import com.axiel7.anihyou.core.common.utils.ContextUtils.openActionView
import com.axiel7.anihyou.core.network.MediaDetailsQuery
import com.axiel7.anihyou.core.resources.R

/**
 * Watch tab — displays the AniList streamingEpisodes list (titles + thumbnails from CR/etc.)
 * and opens each episode in Miruro via the system browser.
 *
 * No WebView. No crash. No API key needed.
 *
 * How it works:
 *  - AniList's streamingEpisodes field returns per-episode thumbnails + titles scraped from
 *    streaming sites. We display those for a rich episode grid.
 *  - When the user taps an episode, we build a miruro.to/watch URL with the AniList ID +
 *    episode number and open it in the system browser (Chrome/Firefox/etc.) where it works
 *    perfectly unlike WebView.
 *  - If AniList has no streamingEpisodes data (some older/less popular anime), we fall back
 *    to a plain numbered list using the totalEpisodes count.
 */
@Composable
fun WatchView(
    anilistId: Int,
    totalEpisodes: Int?,
    userProgress: Int?,
    streamingEpisodes: List<MediaDetailsQuery.StreamingEpisode?>?,
    isDub: Boolean,
    onToggleDub: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val episodeCount = totalEpisodes?.takeIf { it > 0 } ?: streamingEpisodes?.size ?: 0
    val startEp = (userProgress ?: 0).coerceAtLeast(1).coerceAtMost(episodeCount.coerceAtLeast(1))

    var selectedEpisode by rememberSaveable { mutableIntStateOf(startEp) }
    val listState = rememberLazyListState()

    // Build a lookup map: episode number -> StreamingEpisode (for thumbnail + title)
    val episodeMap = streamingEpisodes
        ?.filterNotNull()
        ?.associateBy { ep ->
            // AniList titles look like "Episode 1 - Title Name" — extract the number
            val match = Regex("\\d+").find(ep.title ?: "")
            match?.value?.toIntOrNull() ?: 0
        }
        ?: emptyMap()

    Column(modifier = modifier.fillMaxSize()) {

        // ── Sub / Dub toggle ────────────────────────────────────────────────
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

        // ── "Watch Episode N on Miruro" CTA button ──────────────────────────
        Button(
            onClick = {
                val lang = if (isDub) "dub" else "sub"
                val url = "$MIRURO_BASE_URL/watch?id=$anilistId&ep=$selectedEpisode&lang=$lang"
                context.openActionView(url)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(
                painter = painterResource(R.drawable.play_circle_24),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("Watch Episode $selectedEpisode on Miruro")
        }

        // ── Episode list ─────────────────────────────────────────────────────
        if (episodeCount == 0) {
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
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                val count = episodeCount.coerceAtLeast(episodeMap.size)
                itemsIndexed(List(count) { it + 1 }) { _, epNum ->
                    val streamEp = episodeMap[epNum]
                    val isSelected = epNum == selectedEpisode
                    val isWatched = epNum < (userProgress ?: 0)

                    WatchEpisodeItem(
                        episodeNumber = epNum,
                        title = streamEp?.title,
                        thumbnail = streamEp?.thumbnail,
                        isSelected = isSelected,
                        isWatched = isWatched,
                        onClick = { selectedEpisode = epNum },
                    )
                }
            }
        }
    }
}

@Composable
private fun WatchEpisodeItem(
    episodeNumber: Int,
    title: String?,
    thumbnail: String?,
    isSelected: Boolean,
    isWatched: Boolean,
    onClick: () -> Unit,
) {
    val bgColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isWatched  -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        else       -> MaterialTheme.colorScheme.surface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = bgColor,
        tonalElevation = if (isSelected) 4.dp else 0.dp,
        shape = RoundedCornerShape(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Thumbnail or fallback numbered box
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                if (thumbnail != null) {
                    AsyncImage(
                        model = thumbnail,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    Text(
                        text = "$episodeNumber",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                    )
                }

                // Play overlay icon when selected
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.play_circle_24),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                // Watched checkmark badge
                if (isWatched) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(18.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✓",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))

            // Title column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.episode_n, episodeNumber),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                )
                if (title != null) {
                    Spacer(Modifier.height(2.dp))
                    // Strip the "Episode N - " prefix AniList adds
                    val cleanTitle = title.replace(Regex("^Episode \\d+ - "), "")
                    Text(
                        text = cleanTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}
