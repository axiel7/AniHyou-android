package com.axiel7.anihyou.ui.screens.characterdetails.content

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.common.TranslateIconButton
import com.axiel7.anihyou.ui.composables.common.singleClick
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_BIG
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.screens.characterdetails.CharacterDetailsUiState
import com.axiel7.anihyou.utils.ContextUtils.copyToClipBoard
import com.axiel7.anihyou.utils.DateUtils.formatted
import com.axiel7.anihyou.utils.LocaleUtils.LocalIsLanguageEn
import io.github.fornewid.placeholder.material3.placeholder

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterInfoView(
    uiState: CharacterDetailsUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToFullscreenImage: (String) -> Unit,
) {
    val context = LocalContext.current
    var showSpoiler by remember { mutableStateOf(false) }
    val isCurrentLanguageEn = LocalIsLanguageEn.current

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
                url = uiState.character?.image?.large,
                modifier = Modifier
                    .padding(16.dp)
                    .size(PERSON_IMAGE_SIZE_BIG.dp)
                    .clickable(onClick = singleClick {
                        uiState.character?.image?.large?.let(navigateToFullscreenImage)
                    }),
                showShadow = true
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.character?.name?.userPreferred ?: "Loading",
                    modifier = Modifier
                        .padding(8.dp)
                        .defaultPlaceholder(visible = uiState.isLoading)
                        .combinedClickable(
                            onLongClick = {
                                uiState.character?.name?.userPreferred?.let {
                                    context.copyToClipBoard(it)
                                }
                            },
                            onClick = {}
                        ),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                SelectionContainer {
                    if (!uiState.character?.name?.native.isNullOrBlank() || uiState.isLoading) {
                        Text(
                            text = uiState.character?.name?.native ?: "Loading...",
                            modifier = Modifier
                                .padding(8.dp)
                                .defaultPlaceholder(visible = uiState.isLoading),
                        )
                    }

                    if (uiState.alternativeNames?.isNotBlank() == true) {
                        Text(
                            text = uiState.alternativeNames,
                            modifier = Modifier
                                .padding(8.dp)
                                .defaultPlaceholder(visible = uiState.isLoading),
                        )
                    }

                    if (uiState.alternativeNamesSpoiler?.isNotBlank() == true) {
                        Text(
                            text = uiState.alternativeNamesSpoiler,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .placeholder(visible = !showSpoiler)
                                .clickable { showSpoiler = !showSpoiler }
                        )
                    }
                }
            }//: Column
        }//: Row

        InfoItemView(
            title = stringResource(R.string.birthday),
            info = uiState.character?.dateOfBirth?.fuzzyDate?.formatted(),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.age),
            info = uiState.character?.age,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.gender),
            info = uiState.character?.gender,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.blood_type),
            info = uiState.character?.bloodType,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )

        if (uiState.isLoading) {
            Text(
                text = stringResource(R.string.lorem_ipsun),
                modifier = Modifier
                    .padding(16.dp)
                    .defaultPlaceholder(visible = true),
                lineHeight = 18.sp
            )
        } else if (uiState.character?.description != null) {
            DefaultMarkdownText(
                markdown = uiState.character.description,
                modifier = Modifier.padding(16.dp)
            )
            if (!isCurrentLanguageEn) {
                TranslateIconButton(
                    text = uiState.character.description,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }//: Column
}