package com.axiel7.anihyou.ui.screens.characterdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.character.localized
import com.axiel7.anihyou.ui.base.TabRowItem
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.DefaultTabRowWithPager
import com.axiel7.anihyou.ui.composables.FavoriteIconButton
import com.axiel7.anihyou.ui.composables.HtmlWebView
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.OnBottomReached
import com.axiel7.anihyou.ui.composables.ShareIconButton
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_BIG
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.axiel7.anihyou.utils.DateUtils.formatted
import com.google.accompanist.placeholder.material.placeholder
import kotlinx.coroutines.launch

private enum class CharacterInfoType {
    INFO, MEDIA;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(INFO, title = R.string.information, icon = R.drawable.info_24),
            TabRowItem(MEDIA, title = R.string.character_media, icon = R.drawable.movie_24),
        )
    }
}

const val CHARACTER_DETAILS_DESTINATION = "character/{id}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsView(
    characterId: Int,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
    navigateToFullscreenImage: (String?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel = viewModel { CharacterDetailsViewModel(characterId) }
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        actions = {
            FavoriteIconButton(
                isFavorite = viewModel.characterDetails?.isFavourite ?: false,
                favoritesCount = viewModel.characterDetails?.favourites ?: 0,
                onClick = {
                    scope.launch { viewModel.toggleFavorite() }
                }
            )
            ShareIconButton(url = viewModel.characterDetails?.siteUrl ?: "")
        },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        DefaultTabRowWithPager(
            tabs = CharacterInfoType.tabRows,
            modifier = Modifier
                .padding(
                    start = padding.calculateStartPadding(LocalLayoutDirection.current),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            when (CharacterInfoType.tabRows[it].value) {
                CharacterInfoType.INFO ->
                    CharacterInfoView(
                        viewModel = viewModel,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToFullscreenImage = navigateToFullscreenImage,
                    )

                CharacterInfoType.MEDIA ->
                    CharacterMediaView(
                        viewModel = viewModel,
                        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                        contentPadding = PaddingValues(
                            bottom = padding.calculateBottomPadding()
                        ),
                        navigateToMediaDetails = navigateToMediaDetails
                    )
            }
        }
    }//: Scaffold
}

@Composable
fun CharacterInfoView(
    viewModel: CharacterDetailsViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToFullscreenImage: (String?) -> Unit,
) {
    var showSpoiler by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        if (viewModel.characterDetails == null)
            viewModel.getCharacterDetails()
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonImage(
                url = viewModel.characterDetails?.image?.large,
                modifier = Modifier
                    .padding(16.dp)
                    .size(PERSON_IMAGE_SIZE_BIG.dp)
                    .clickable {
                        navigateToFullscreenImage(viewModel.characterDetails?.image?.large)
                    },
                showShadow = true
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = viewModel.characterDetails?.name?.userPreferred ?: "Loading",
                    modifier = Modifier
                        .padding(8.dp)
                        .defaultPlaceholder(visible = viewModel.isLoading),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (!viewModel.characterDetails?.name?.native.isNullOrBlank() || viewModel.isLoading) {
                    Text(
                        text = viewModel.characterDetails?.name?.native ?: "Loading...",
                        modifier = Modifier
                            .padding(8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading),
                    )
                }

                if (viewModel.alternativeNames?.isNotBlank() == true) {
                    Text(
                        text = viewModel.alternativeNames ?: "",
                        modifier = Modifier
                            .padding(8.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading),
                    )
                }

                if (viewModel.alternativeNamesSpoiler?.isNotBlank() == true) {
                    Text(
                        text = viewModel.alternativeNamesSpoiler ?: "",
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .placeholder(visible = !showSpoiler)
                            .clickable { showSpoiler = !showSpoiler }
                    )
                }
            }//: Column
        }//: Row

        InfoItemView(
            title = stringResource(R.string.birthday),
            info = viewModel.characterDetails?.dateOfBirth?.fuzzyDate?.formatted(),
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.age),
            info = viewModel.characterDetails?.age,
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.gender),
            info = viewModel.characterDetails?.gender,
            modifier = Modifier.defaultPlaceholder(visible = viewModel.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.blood_type),
            info = viewModel.characterDetails?.bloodType,
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
        } else if (viewModel.characterDetails?.description != null) {
            HtmlWebView(html = viewModel.characterDetails!!.description!!)
        }
    }//: Column
}

@Composable
fun CharacterMediaView(
    viewModel: CharacterDetailsViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToMediaDetails: (Int) -> Unit,
) {
    val listState = rememberLazyListState()

    listState.OnBottomReached(buffer = 3) {
        if (viewModel.hasNextPage) viewModel.getCharacterMedia()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = contentPadding,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(
            items = viewModel.characterMedia,
            key = { it.id!! },
            contentType = { it }
        ) { item ->
            MediaItemHorizontal(
                title = item.node?.title?.userPreferred ?: "",
                imageUrl = item.node?.coverImage?.large,
                subtitle1 = {
                    Text(
                        text = item.characterRole?.localized() ?: "",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 15.sp
                    )
                },
                subtitle2 = {
                    Text(
                        text = item.voiceActors
                            ?.joinToString { "${it?.name?.userPreferred} (${it?.languageV2})" }
                            ?: "",
                        color = MaterialTheme.colorScheme.outline,
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
        } else if (viewModel.characterMedia.isEmpty()) {
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
fun CharacterDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            CharacterDetailsView(
                characterId = 1,
                navigateBack = {},
                navigateToMediaDetails = {},
                navigateToFullscreenImage = {}
            )
        }
    }
}