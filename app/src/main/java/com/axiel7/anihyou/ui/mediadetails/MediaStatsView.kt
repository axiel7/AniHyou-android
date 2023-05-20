package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.stats.HorizontalStatsBar
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun MediaStatsView(
    mediaId: Int,
    viewModel: MediaDetailsViewModel,
) {
    LaunchedEffect(mediaId) {
        viewModel.getMediaStats(mediaId)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        InfoTitle(text = stringResource(R.string.status_distribution))
        HorizontalStatsBar(
            stats = viewModel.mediaStatusDistribution,
            horizontalPadding = 8.dp
        )
    }
}

@Preview
@Composable
fun MediaStatsViewPreview() {
    AniHyouTheme {
        Surface {
            MediaStatsView(
                mediaId = 1,
                viewModel = viewModel()
            )
        }
    }
}