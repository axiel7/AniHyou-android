package com.axiel7.anihyou.feature.mediadetails.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.core.model.media.AnimeSeason
import com.axiel7.anihyou.core.ui.composables.InfoTitle
import com.axiel7.anihyou.feature.mediadetails.Episode
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsUiState

@Composable
fun EpisodesView(
    uiState: MediaDetailsUiState,
    onEpisodeClick: (Episode) -> Unit,
) {
    if (uiState.isLoadingEpisodes) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Loading episodes...")
        }
    } else if (uiState.seasonsWithEpisodes.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "No episodes found.")
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.seasonsWithEpisodes) { seasonWithEpisodes ->
                val season = seasonWithEpisodes.season
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = season.localized(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    seasonWithEpisodes.episodes.forEach { episode ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEpisodeClick(episode) }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = (episode.number.toString() + if (episode.title != null) ": ${episode.title}" else ""),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider()
                    }
                }
                Spacer(modifier = Modifier.padding(bottom = 16.dp))
            }
        }
    }
}
