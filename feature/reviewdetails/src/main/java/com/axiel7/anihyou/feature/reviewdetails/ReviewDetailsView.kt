package com.axiel7.anihyou.feature.reviewdetails

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.axiel7.anihyou.core.base.ANILIST_REVIEW_URL
import com.axiel7.anihyou.core.model.review.userRatingsString
import com.axiel7.anihyou.core.network.type.ReviewRating
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.navigation.NavActionManager
import com.axiel7.anihyou.core.ui.common.navigation.Routes
import com.axiel7.anihyou.core.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.core.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.core.ui.composables.common.BackIconButton
import com.axiel7.anihyou.core.ui.composables.common.LikeButton
import com.axiel7.anihyou.core.ui.composables.common.OpenInBrowserIconButton
import com.axiel7.anihyou.core.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.core.ui.composables.webview.HtmlWebView
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ReviewDetailsView(
    arguments: Routes.ReviewDetails,
    navActionManager: NavActionManager
) {
    val viewModel: ReviewDetailsViewModel = koinViewModel(parameters = { parametersOf(arguments) })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ReviewDetailsContent(
        uiState = uiState,
        event = viewModel,
        navActionManager = navActionManager,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReviewDetailsContent(
    uiState: ReviewDetailsUiState,
    event: ReviewDetailsEvent?,
    navActionManager: NavActionManager,
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = uiState.details?.user?.name ?: stringResource(R.string.loading),
        navigationIcon = { BackIconButton(onClick = navActionManager::goBack) },
        actions = {
            OpenInBrowserIconButton(url = ANILIST_REVIEW_URL + uiState.details?.id)
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
                    html = uiState.details?.body.orEmpty()
                )
            }

            TextSubtitleVertical(
                text = "${uiState.details?.score}/100",
                subtitle = stringResource(R.string.score),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                isLoading = uiState.isLoading
            )

            // Ratings
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextSubtitleVertical(
                    text = uiState.details?.userRatingsString(),
                    subtitle = stringResource(R.string.users_likes),
                    isLoading = uiState.isLoading
                )
                val isUpvote = uiState.details?.userRating == ReviewRating.UP_VOTE
                LikeButton(
                    isLiked = isUpvote,
                    onClick = {
                        event?.rateReview(
                            rating = if (isUpvote) ReviewRating.NO_VOTE else ReviewRating.UP_VOTE
                        )
                    }
                )
                val isDownvote = uiState.details?.userRating == ReviewRating.DOWN_VOTE
                LikeButton(
                    isLiked = isDownvote,
                    isDislike = true,
                    onClick = {
                        event?.rateReview(
                            rating = if (isDownvote) ReviewRating.NO_VOTE else ReviewRating.DOWN_VOTE
                        )
                    }
                )
            }
        }//:Column
    }
}

@Preview
@Composable
fun ReviewDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            ReviewDetailsContent(
                uiState = ReviewDetailsUiState(),
                event = null,
                navActionManager = NavActionManager.rememberNavActionManager()
            )
        }
    }
}