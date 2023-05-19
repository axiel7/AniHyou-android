package com.axiel7.anihyou.ui.characterdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.localized
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.HtmlWebView
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.media.MediaItemHorizontal
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_BIG
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme
import com.google.accompanist.placeholder.material.placeholder

const val CHARACTER_DETAILS_DESTINATION = "character/{id}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsView(
    characterId: Int,
    navigateBack: () -> Unit,
    navigateToMediaDetails: (Int) -> Unit,
) {
    val viewModel: CharacterDetailsViewModel = viewModel()
    val topAppBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )
    var showSpoiler by remember { mutableStateOf(false) }
    
    LaunchedEffect(characterId) {
        viewModel.getCharacterDetails(characterId)
        viewModel.getCharacterMedia(characterId)
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        ) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PersonImage(
                        url = viewModel.characterDetails?.image?.large,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(PERSON_IMAGE_SIZE_BIG.dp),
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
                        Text(
                            text = viewModel.alternativeNames ?: "Loading...",
                            modifier = Modifier
                                .padding(8.dp)
                                .defaultPlaceholder(visible = viewModel.isLoading),
                        )
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

                if (viewModel.characterDetails?.description == null) {
                    Text(
                        text = stringResource(R.string.lorem_ipsun),
                        modifier = Modifier
                            .padding(16.dp)
                            .defaultPlaceholder(visible = viewModel.isLoading),
                        lineHeight = 18.sp
                    )
                } else {
                    HtmlWebView(html = viewModel.characterDetails!!.description!!)
                }
            }

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
                                ?.joinToString { "${it?.name?.userPreferred} (${it?.languageV2})" } ?: "",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = 15.sp
                        )
                    },
                    onClick = {
                        navigateToMediaDetails(item.node?.id!!)
                    }
                )
            }
        }//: Column
    }//: Scaffold
}

@Preview
@Composable
fun CharacterDetailsViewPreview() {
    AniHyouTheme {
        Surface {
            CharacterDetailsView(
                characterId = 1,
                navigateBack = {},
                navigateToMediaDetails = {}
            )
        }
    }
}