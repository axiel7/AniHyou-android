package com.axiel7.anihyou.ui.staffdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.yearsActiveFormatted
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DefaultTabRowWithPager
import com.axiel7.anihyou.ui.composables.HtmlWebView
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.ShareIconButton
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_BIG
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.formatted
import com.axiel7.anihyou.utils.StringUtils.toStringOrNull

private enum class StaffInfoType {
    INFO, MEDIA, CHARACTER;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(INFO, title = R.string.information, icon = R.drawable.info_24),
            TabRowItem(MEDIA, title = R.string.character_media, icon = R.drawable.movie_24),
            TabRowItem(CHARACTER, title = R.string.characters, icon = R.drawable.record_voice_over_24),
        )
    }
}

const val STAFF_DETAILS_DESTINATION = "staff/{id}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDetailsView(
    staffId: Int,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String?) -> Unit,
) {
    val viewModel: StaffDetailsViewModel = viewModel()

    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            ShareIconButton(url = viewModel.staffDetails?.siteUrl ?: "")
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        DefaultTabRowWithPager(
            tabs = StaffInfoType.tabRows,
            modifier = Modifier.padding(padding)
        ) {
            when (StaffInfoType.tabRows[it].value) {
                StaffInfoType.INFO ->
                    StaffInfoView(
                        staffId = staffId,
                        viewModel = viewModel,
                        navigateToFullscreenImage = navigateToFullscreenImage
                    )
                StaffInfoType.MEDIA ->
                    StaffMediaView(
                        staffId = staffId,
                        viewModel = viewModel,
                        navigateToMediaDetails = navigateToMediaDetails
                    )
                StaffInfoType.CHARACTER ->
                    StaffCharacterView(
                        staffId = staffId,
                        viewModel = viewModel,
                        navigateToCharacterDetails = navigateToCharacterDetails
                    )
            }
        }
    }
}

@Composable
fun StaffInfoView(
    staffId: Int,
    viewModel: StaffDetailsViewModel,
    modifier: Modifier = Modifier,
    navigateToFullscreenImage: (String?) -> Unit,
) {
    LaunchedEffect(staffId) {
        viewModel.getStaffDetails(staffId)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonImage(
                url = viewModel.staffDetails?.image?.large,
                modifier = Modifier
                    .padding(16.dp)
                    .size(PERSON_IMAGE_SIZE_BIG.dp)
                    .clickable {
                        navigateToFullscreenImage(viewModel.staffDetails?.image?.large)
                    },
                showShadow = true
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = viewModel.staffDetails?.name?.userPreferred ?: "Loading",
                    modifier = Modifier
                        .padding(8.dp)
                        .defaultPlaceholder(visible = viewModel.isLoading),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (!viewModel.staffDetails?.name?.native.isNullOrBlank() || viewModel.isLoading) {
                    Text(
                        text = viewModel.staffDetails?.name?.native ?: "Loading...",
                        modifier = Modifier
                            .padding(8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading),
                    )
                }
                if (!viewModel.staffDetails?.name?.alternative.isNullOrEmpty()) {
                    Text(
                        text = viewModel.staffDetails?.name?.alternative?.joinToString() ?: "",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }//: Column
        }//: Row

        InfoItemView(
            title = stringResource(R.string.birthday),
            info = viewModel.staffDetails?.dateOfBirth?.fuzzyDate?.formatted(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.age),
            info = viewModel.staffDetails?.age.toStringOrNull(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.gender),
            info = viewModel.staffDetails?.gender,
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.blood_type),
            info = viewModel.staffDetails?.bloodType,
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.years_active),
            info = viewModel.staffDetails?.yearsActiveFormatted(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.hometown),
            info = viewModel.staffDetails?.homeTown,
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.occupations),
            info = viewModel.staffDetails?.primaryOccupations?.filterNotNull()?.joinToString(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )

        if (viewModel.isLoading) {
            Text(
                text = stringResource(R.string.lorem_ipsun),
                modifier = Modifier
                    .padding(16.dp)
                    .defaultPlaceholder(visible = true),
                lineHeight = 18.sp
            )
        } else if (viewModel.staffDetails?.description != null) {
            HtmlWebView(html = viewModel.staffDetails!!.description!!)
        }
    }//: Column
}

@Composable
fun StaffMediaView(
    staffId: Int,
    viewModel: StaffDetailsViewModel,
    modifier: Modifier = Modifier,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPageMedia) viewModel.getStaffMedia(staffId)
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(
            items = viewModel.staffMedia,
            key = { it.id!! },
            contentType = { it }
        ) { item ->
            MediaItemHorizontal(
                title = item.node?.title?.userPreferred ?: "",
                imageUrl = item.node?.coverImage?.large,
                subtitle1 = {
                    Text(
                        text = item.staffRole ?: "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                },
                onClick = {
                    navigateToMediaDetails(item.node?.id!!)
                }
            )
        }
        if (viewModel.isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        } else if (viewModel.staffMedia.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun StaffCharacterView(
    staffId: Int,
    viewModel: StaffDetailsViewModel,
    modifier: Modifier = Modifier,
    navigateToCharacterDetails: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPageCharacter) viewModel.getStaffCharacters(staffId)
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
    ) {
        items(
            items = viewModel.staffCharacters,
            key = { it.id!! },
            contentType = { it }
        ) { item ->
            item.characters?.forEach { character ->
                PersonItemHorizontal(
                    title = character?.name?.userPreferred ?: "",
                    modifier = Modifier.fillMaxWidth(),
                    imageUrl = character?.image?.large,
                    subtitle = item.node?.title?.userPreferred ?: "",
                    onClick = {
                        navigateToCharacterDetails(character!!.id)
                    }
                )
            }
        }
        if (viewModel.isLoading) {
            items(10) {
                MediaItemHorizontalPlaceholder()
            }
        } else if (viewModel.staffCharacters.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_information),
                    modifier = Modifier.padding(16.dp)
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