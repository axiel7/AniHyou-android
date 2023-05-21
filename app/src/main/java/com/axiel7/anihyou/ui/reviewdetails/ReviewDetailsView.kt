package com.axiel7.anihyou.ui.reviewdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.HtmlWebView
import com.axiel7.anihyou.ui.composables.TextSubtitleVertical
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.theme.AniHyouTheme

const val REVIEW_DETAILS_DESTINATION = "review/{id}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailsView(
    reviewId: Int,
    navigateBack: () -> Unit,
) {
    val viewModel: ReviewDetailsViewModel = viewModel()
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    LaunchedEffect(reviewId) {
        viewModel.getReviewDetails(reviewId)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = viewModel.reviewDetails?.user?.name ?: stringResource(R.string.loading),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
        ) {
            if (viewModel.isLoading) {
                Text(
                    text = stringResource(R.string.lorem_ipsun),
                    modifier = Modifier
                        .padding(16.dp)
                        .defaultPlaceholder(visible = true),
                    lineHeight = 22.sp,
                )
            } else {
                HtmlWebView(
                    html = viewModel.reviewDetails?.body ?: ""
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TextSubtitleVertical(
                    text = "${viewModel.reviewDetails?.score}/100",
                    subtitle = stringResource(R.string.score),
                    isLoading = viewModel.isLoading
                )
                TextSubtitleVertical(
                    text = "${viewModel.userAcceptance}%",
                    subtitle = stringResource(R.string.users_likes),
                    isLoading = viewModel.isLoading
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
                reviewId = 1,
                navigateBack = {}
            )
        }
    }
}