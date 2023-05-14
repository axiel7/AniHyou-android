package com.axiel7.anihyou.ui.mediadetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme

@Composable
fun MediaRelationsView(
    viewModel: MediaDetailsViewModel
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = "Coming soon")
    }
}

@Preview
@Composable
fun MediaRelationsViewPreview() {
    AniHyouTheme {
        Surface {
            MediaRelationsView(
                viewModel = viewModel()
            )
        }
    }
}