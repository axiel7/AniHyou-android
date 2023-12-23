package com.axiel7.anihyou.ui.screens.mediadetails.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.character.localized
import com.axiel7.anihyou.ui.composables.InfoTitle
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_SMALL
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontal
import com.axiel7.anihyou.ui.composables.person.PersonItemHorizontalPlaceholder
import com.axiel7.anihyou.ui.screens.mediadetails.MediaDetailsUiState
import com.axiel7.anihyou.ui.theme.AniHyouTheme

private const val GRID_HEIGHT = (PERSON_IMAGE_SIZE_SMALL + 16) * 2

@Composable
fun MediaCharacterStaffView(
    uiState: MediaDetailsUiState,
    fetchData: () -> Unit,
    navigateToCharacterDetails: (Int) -> Unit,
    navigateToStaffDetails: (Int) -> Unit,
) {
    val isLoading = uiState.charactersAndStaff == null

    LaunchedEffect(uiState.charactersAndStaff) {
        if (uiState.charactersAndStaff == null) fetchData()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Staff
        val mediaStaff = uiState.charactersAndStaff?.staff.orEmpty()
        if (isLoading || mediaStaff.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.staff))
            Box(
                modifier = Modifier
                    .height(GRID_HEIGHT.dp)
            ) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2)
                ) {
                    if (isLoading) {
                        items(6) {
                            PersonItemHorizontalPlaceholder()
                        }
                    }
                    items(
                        items = mediaStaff,
                        contentType = { it }
                    ) { item ->
                        PersonItemHorizontal(
                            title = item.mediaStaff.node?.name?.userPreferred.orEmpty(),
                            imageUrl = item.mediaStaff.node?.image?.medium,
                            subtitle = item.mediaStaff.role,
                            onClick = {
                                navigateToStaffDetails(item.mediaStaff.node!!.id)
                            }
                        )
                    }
                }//: Grid
            }//: Box
        }

        // Characters
        val mediaCharacters = uiState.charactersAndStaff?.characters.orEmpty()
        if (isLoading || mediaCharacters.isNotEmpty()) {
            InfoTitle(text = stringResource(R.string.characters))
            Box(
                modifier = Modifier
                    .height(GRID_HEIGHT.dp)
            ) {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2)
                ) {
                    if (isLoading) {
                        items(6) {
                            PersonItemHorizontalPlaceholder()
                        }
                    }
                    items(mediaCharacters) { item ->
                        PersonItemHorizontal(
                            title = item.mediaCharacter.node?.name?.userPreferred.orEmpty(),
                            imageUrl = item.mediaCharacter.node?.image?.medium,
                            subtitle = item.mediaCharacter.role?.localized(),
                            onClick = {
                                navigateToCharacterDetails(item.mediaCharacter.node!!.id)
                            }
                        )
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
                navigateToStaffDetails = {}
            )
        }
    }
}