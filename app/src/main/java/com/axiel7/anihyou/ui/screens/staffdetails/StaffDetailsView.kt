package com.axiel7.anihyou.ui.screens.staffdetails

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DefaultTabRowWithPager
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.ShareIconButton
import com.axiel7.anihyou.ui.screens.staffdetails.content.StaffCharacterView
import com.axiel7.anihyou.ui.screens.staffdetails.content.StaffInfoView
import com.axiel7.anihyou.ui.screens.staffdetails.content.StaffMediaView
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import kotlinx.coroutines.launch

private enum class StaffInfoType {
    INFO, MEDIA, CHARACTER;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(INFO, title = R.string.information, icon = R.drawable.info_24),
            TabRowItem(MEDIA, title = R.string.character_media, icon = R.drawable.movie_24),
            TabRowItem(
                CHARACTER,
                title = R.string.characters,
                icon = R.drawable.record_voice_over_24
            ),
        )
    }
}

const val STAFF_ID_ARGUMENT = "{id}"
const val STAFF_DETAILS_DESTINATION = "staff/$STAFF_ID_ARGUMENT"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDetailsView(
    staffId: Int,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = viewModel { StaffDetailsViewModel(staffId) }

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = viewModel.staffDetails?.isFavourite ?: false,
                favoritesCount = viewModel.staffDetails?.favourites ?: 0,
                onClick = {
                    scope.launch { viewModel.toggleFavorite() }
                }
            )
            ShareIconButton(url = viewModel.staffDetails?.siteUrl ?: "")
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        DefaultTabRowWithPager(
            tabs = StaffInfoType.tabRows,
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            when (StaffInfoType.tabRows[it].value) {
                StaffInfoType.INFO ->
                    StaffInfoView(
                        staffId = staffId,
                        viewModel = viewModel,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToFullscreenImage = navigateToFullscreenImage
                    )

                StaffInfoType.MEDIA ->
                    StaffMediaView(
                        viewModel = viewModel,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToMediaDetails = navigateToMediaDetails
                    )

                StaffInfoType.CHARACTER ->
                    StaffCharacterView(
                        viewModel = viewModel,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToCharacterDetails = navigateToCharacterDetails
                    )
            }
        }
    }
}

@Preview
@Composable
fun StaffDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            StaffDetailsView(
                staffId = 0,
                navigateBack = {},
                navigateToMediaDetails = {},
                navigateToCharacterDetails = {},
                navigateToFullscreenImage = {}
            )
        }
    }
}