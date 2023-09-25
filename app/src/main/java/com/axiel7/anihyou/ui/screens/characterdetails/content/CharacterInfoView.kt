package com.axiel7.anihyou.ui.screens.characterdetails.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_BIG
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.composables.webview.HtmlWebView
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetailsViewModel
import com.axiel7.anihyou.utils.DateUtils.formatted
import io.github.fornewid.placeholder.material3.placeholder

@Composable
fun CharacterInfoView(
    viewModel: CharacterDetailsViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToFullscreenImage: (String) -> Unit,
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
                        viewModel.characterDetails?.image?.large?.let {
                            navigateToFullscreenImage(it)
                        }
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
            HtmlWebView(
                html = viewModel.characterDetails!!.description!!,
                hardwareEnabled = false
            )
        }
    }//: Column
}