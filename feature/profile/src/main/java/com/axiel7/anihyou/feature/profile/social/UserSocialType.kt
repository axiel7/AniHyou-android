package com.axiel7.anihyou.feature.profile.social

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.core.model.base.Localizable
import com.axiel7.anihyou.core.resources.R

enum class UserSocialType : Localizable {
    FOLLOWERS, FOLLOWING;

    @Composable
    override fun localized() = when (this) {
        FOLLOWERS -> stringResource(R.string.followers)
        FOLLOWING -> stringResource(R.string.following)
    }
}