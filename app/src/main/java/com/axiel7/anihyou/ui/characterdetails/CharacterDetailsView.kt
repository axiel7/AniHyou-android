package com.axiel7.anihyou.ui.characterdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.axiel7.anihyou.ui.composables.BackIconButton
import com.axiel7.anihyou.ui.composables.DefaultScaffoldWithSmallTopAppBar
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_BIG
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.theme.AniHyouTheme

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
    }

    DefaultScaffoldWithSmallTopAppBar(
        title = "",
        navigationIcon = { BackIconButton(onClick = navigateBack) },
        scrollBehavior = topAppBarScrollBehavior
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.height((PERSON_IMAGE_SIZE_BIG + 36).dp)
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
                    if (showSpoiler && viewModel.alternativeNamesSpoiler?.isNotBlank() == true) {
                        Text(
                            text = viewModel.alternativeNamesSpoiler ?: "",
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }//: Column
            }//: Row
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