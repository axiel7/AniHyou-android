package com.axiel7.anihyou.ui.screens.reviewdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.repository.DataResult
import com.axiel7.anihyou.data.repository.ReviewRepository.userAcceptance
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
    val viewModel = viewModel { ReviewDetailsViewModel(reviewId) }
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        rememberTopAppBarState()
    )

    val reviewDetailsState by viewModel.reviewDetails.collectAsState()
    val reviewDetails by remember {
        derivedStateOf { (reviewDetailsState as? DataResult.Success)?.data }
    }
    val isLoading by remember {
        derivedStateOf { reviewDetailsState is DataResult.Loading }
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = reviewDetails?.user?.name ?: stringResource(R.string.loading),
        navigationIcon = { BackIconButton(onClick = navigateBack) },
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
                text = reviewDetails?.summary ?: "Loading review",
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .defaultPlaceholder(visible = isLoading),
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Body
            if (isLoading) {
                Text(
                    text = stringResource(R.string.lorem_ipsun),
                    modifier = Modifier
                        .padding(16.dp)
                        .defaultPlaceholder(visible = true),
                    lineHeight = 22.sp,
                )
            } else {
                HtmlWebView(
                    html = reviewDetails?.body ?: ""
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
                    text = "${reviewDetails?.score}/100",
                    subtitle = stringResource(R.string.score),
                    isLoading = isLoading
                )
                TextSubtitleVertical(
                    text = "${reviewDetails?.userAcceptance()}% (${reviewDetails?.rating ?: 0}/${reviewDetails?.ratingAmount ?: 0})",
                    subtitle = stringResource(R.string.users_likes),
                    isLoading = isLoading
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