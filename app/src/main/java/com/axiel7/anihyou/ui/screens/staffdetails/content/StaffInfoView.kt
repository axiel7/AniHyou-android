package com.axiel7.anihyou.ui.screens.staffdetails.content

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.staff.yearsActiveFormatted
import com.axiel7.anihyou.ui.composables.HtmlWebView
import com.axiel7.anihyou.ui.composables.InfoItemView
import com.axiel7.anihyou.ui.composables.defaultPlaceholder
import com.axiel7.anihyou.ui.composables.person.PERSON_IMAGE_SIZE_BIG
import com.axiel7.anihyou.ui.composables.person.PersonImage
import com.axiel7.anihyou.ui.screens.staffdetails.StaffDetailsViewModel
import com.axiel7.anihyou.utils.DateUtils.formatted
import com.axiel7.anihyou.utils.StringUtils.toStringOrNull

@Composable
fun StaffInfoView(
    staffId: Int,
    viewModel: StaffDetailsViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    navigateToFullscreenImage: (String?) -> Unit,
) {
    LaunchedEffect(staffId) {
        viewModel.getStaffDetails()
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