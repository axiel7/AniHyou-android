package com.axiel7.anihyou.ui.screens.profile.activity

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.data.model.activity.text
import com.axiel7.anihyou.ui.composables.list.OnBottomReached
import com.axiel7.anihyou.ui.screens.profile.ProfileViewModel
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.DateUtils.timestampIntervalSinceNow
import java.time.temporal.ChronoUnit

@Composable
fun UserActivityView(
    viewModel: ProfileViewModel,
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPageActivity)
            viewModel.getUserActivity(viewModel.userId)
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        state = listState,
        contentPadding = PaddingValues(8.dp)
    ) {
        items(
            items = viewModel.userActivities,
            contentType = { it }
        ) { item ->
            item.onListActivity?.listActivityFragment?.let { activity ->
                ActivityItem(
                    title = activity.text(),
                    imageUrl = activity.media?.coverImage?.medium,
                    subtitle = activity.createdAt.toLong().timestampIntervalSinceNow()
                        .secondsToLegibleText(
                            maxUnit = ChronoUnit.WEEKS,
                            isFutureDate = false
                        ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    onClick = {
                        navigateToMediaDetails(activity.media?.id!!)
                    }
                )
            }
        }
        if (viewModel.isLoadingActivity) {
            items(10) {
                ActivityItemPlaceholder(
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }//: LazyColumn
}

@Preview
@Composable
fun UserActivityViewPreview() {
    AniHyouTheme {
        Surface {
            UserActivityView(
                viewModel = viewModel(),
                navigateToMediaDetails = {}
            )
        }
    }
}