package com.axiel7.anihyou.ui.screens.staffdetails.content

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.staff.yearsActiveFormatted
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.common.TranslateIconButton
import com.axiel7.anihyou.ui.composables.common.singleClick
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.markdown.DefaultMarkdownText
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_BIG
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetailsUiState
import com.axiel7.anihyou.utils.ContextUtils.copyToClipBoard
import com.axiel7.anihyou.utils.DateUtils.formatted
import com.axiel7.anihyou.utils.LocaleUtils.LocalIsLanguageEn
import com.axiel7.anihyou.utils.NumberUtils.format

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StaffInfoView(
    uiState: StaffDetailsUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToFullscreenImage: (String) -> Unit,
) {
    val context = LocalContext.current
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
                url = uiState.details?.image?.large,
                modifier = Modifier
                    .padding(16.dp)
                    .size(PERSON_IMAGE_SIZE_BIG.dp)
                    .clickable(onClick = singleClick {
                        uiState.details?.image?.large?.let(navigateToFullscreenImage)
                    }),
                showShadow = true
            )

            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.details?.name?.userPreferred ?: "Loading",
                    modifier = Modifier
                        .padding(8.dp)
                        .defaultPlaceholder(visible = uiState.isLoading)
                        .combinedClickable(
                            onLongClick = {
                                uiState.details?.name?.userPreferred?.let {
                                    context.copyToClipBoard(it)
                                }
                            },
                            onClick = {}
                        ),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )

                if (!uiState.details?.name?.native.isNullOrBlank() || uiState.isLoading) {
                    SelectionContainer {
                        Text(
                            text = uiState.details?.name?.native ?: "Loading...",
                            modifier = Modifier
                                .padding(8.dp)
                                .defaultPlaceholder(visible = uiState.isLoading),
                        )
                    }
                }
                if (!uiState.details?.name?.alternative.isNullOrEmpty()) {
                    SelectionContainer {
                        Text(
                            text = uiState.details.name.alternative.joinToString(),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }//: Column
        }//: Row

        InfoItemView(
            title = stringResource(R.string.birthday),
            info = uiState.details?.dateOfBirth?.fuzzyDate?.formatted(),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.age),
            info = uiState.details?.age?.format(),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.gender),
            info = uiState.details?.gender,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.blood_type),
            info = uiState.details?.bloodType,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.years_active),
            info = uiState.details?.yearsActiveFormatted(),
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.hometown),
            info = uiState.details?.homeTown,
            modifier = Modifier.defaultPlaceholder(visible = uiState.isLoading)
        )
        InfoItemView(
            title = stringResource(R.string.occupations),
            info = uiState.details?.primaryOccupations?.filterNotNull()?.joinToString(),
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
        } else if (uiState.details?.description != null) {
            DefaultMarkdownText(
                markdown = uiState.details.description,
                modifier = Modifier.padding(16.dp)
            )
            if (!isCurrentLanguageEn) {
                TranslateIconButton(
                    text = uiState.details.description,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }//: Column
}