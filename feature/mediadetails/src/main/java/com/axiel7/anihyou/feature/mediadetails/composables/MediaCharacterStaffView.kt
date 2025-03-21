package com.axiel7.anihyou.feature.mediadetails.composables

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.model.character.localized
import com.axiel7.anihyou.core.model.staff.roleLocalized
import com.axiel7.anihyou.core.network.fragment.MediaCharacter
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.composables.InfoTitle
import com.axiel7.anihyou.core.ui.composables.person.PERSON_IMAGE_SIZE_SMALL
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.core.ui.composables.person.PersonItemHorizontalPlaceholder
import com.axiel7.anihyou.core.ui.theme.AniHyouTheme
import com.axiel7.anihyou.feature.mediadetails.MediaDetailsUiState

private const val GRID_HEIGHT = (PERSON_IMAGE_SIZE_SMALL + 16) * 2

@Composable
fun MediaCharacterStaffView(
    uiState: MediaDetailsUiState,
    fetchData: () -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
    showVoiceActorsSheet: (MediaCharacter) -> Unit,
) {
    val staffListState = rememberLazyGridState()
    val charactersListState = rememberLazyGridState()

    LaunchedEffect(uiState.staff) {
        if (uiState.staff == null) fetchData()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Staff
        val isLoadingStaff = uiState.staff == null
        val staff = uiState.staff.orEmpty()
        if (isLoadingStaff || staff.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.staff))
            Box(
                modifier = Modifier
                    .height(GRID_HEIGHT.dp)
            ) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2),
                    state = staffListState,
                    flingBehavior = rememberSnapFlingBehavior(
                        lazyGridState = staffListState,
                        snapPosition = SnapPosition.Start
                    )
                ) {
                    if (isLoadingStaff) {
                        items(6) {
                            PersonItemHorizontalPlaceholder()
                        }
                    }
                    items(
                        items = staff,
                        contentType = { it }
                    ) { item ->
                        PersonItemHorizontal(
                            title = item.node?.name?.userPreferred.orEmpty(),
                            imageUrl = item.node?.image?.medium,
                            subtitle = item.roleLocalized(),
                            onClick = {
                                navigateToStaffDetails(item.node!!.id)
                            }
                        )
                    }
                }//: Grid
            }//: Box
        }

        // Characters
        val isLoadingCharacters = uiState.characters == null
        val characters = uiState.characters.orEmpty()
        if (isLoadingCharacters || characters.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.characters))
            Box(
                modifier = Modifier
                    .height(GRID_HEIGHT.dp)
            ) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2),
                    state = charactersListState,
                    flingBehavior = rememberSnapFlingBehavior(
                        lazyGridState = charactersListState,
                        snapPosition = SnapPosition.Start
                    )
                ) {
                    if (isLoadingCharacters) {
                        items(6) {
                            PersonItemHorizontalPlaceholder()
                        }
                    }
                    items(characters) { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            PersonItemHorizontal(
                                title = item.node?.name?.userPreferred.orEmpty(),
                                modifier = Modifier.width(300.dp),
                                imageUrl = item.node?.image?.medium,
                                subtitle = item.role?.localized(),
                                onClick = {
                                    navigateToCharacterDetails(item.node!!.id)
                                }
                            )
                            if (!item.voiceActors.isNullOrEmpty()) {
                                IconButton(onClick = { showVoiceActorsSheet(item) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.record_voice_over_24),
                                        contentDescription = stringResource(R.string.voice_actors)
                                    )
                                }
                            }
                        }
                    }
                }//: Grid
            }//: Box
        }
    }//: Column
}

@Preview
@Composable
fun MediaCharacterStaffViewPreview() {
    AniHyouTheme {
        Surface {
            MediaCharacterStaffView(
                uiState = MediaDetailsUiState(),
                fetchData = {},
                navigateToCharacterDetails = {},
                navigateToStaffDetails = {},
                showVoiceActorsSheet = {}
            )
        }
    }
}