package com.axiel7.anihyou.ui.screens.reviewdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.review.userRatingsString
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.common.BackIconButton
import com.axiel7.anihyou.ui.composables.common.OpenInBrowserIconButton
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.webview.HtmlWebView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.ANILIST_REVIEW_URL

const val REVIEW_ID_ARGUMENT = "{id}"
const val REVIEW_DETAILS_DESTINATION = "review/$REVIEW_ID_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailsView(
    navigateBack: () -> Unit,
) {
    val viewModel: ReviewDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = uiState.details?.user?.name ?: stringResource(R.string.loading),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            OpenInBrowserIconButton(url = ANILIST_REVIEW_URL + viewModel.reviewId)
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Title
            Text(
                text = uiState.details?.summary ?: "Loading review",
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .defaultPlaceholder(visible = uiState.isLoading),
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Body
            if (uiState.isLoading) {
                Text(
                    text = stringResource(R.string.lorem_ipsun),
                    modifier = Modifier
                        .padding(16.dp)
                        .defaultPlaceholder(visible = true),
                    lineHeight = 22.sp,
                )
            } else {
                HtmlWebView(
                    html = uiState.details?.body ?: ""
                )
            }

            // Ratings
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextSubtitleVertical(
                    text = "${uiState.details?.score}/100",
                    subtitle = stringResource(R.string.score),
                    isLoading = uiState.isLoading
                )
                TextSubtitleVertical(
                    text = uiState.details?.userRatingsString(),
                    subtitle = stringResource(R.string.users_likes),
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}

@Preview
@Composable
fun ReviewDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            ReviewDetailsView(
                navigateBack = {}
            )
        }
    }
}