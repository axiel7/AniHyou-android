package com.axiel7.anihyou.ui.screens.profile.social

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class UserSocialType : Localizable {
    FOLLOWERS, FOLLOWING;

    @Composable
    override fun localized() = when (this) {
        FOLLOWERS -> stringResource(R.string.followers)
        FOLLOWING -> stringResource(R.string.following)
    }
}