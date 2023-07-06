package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.model.base.Localizable

enum class SearchType : Localizable {
    ANIME {
        @Composable
        override fun localized() = stringResource(R.string.anime)
    },
    MANGA {
        @Composable
        override fun localized() = stringResource(R.string.manga)
    },
    CHARACTER {
        @Composable
        override fun localized() = stringResource(R.string.characters)
    },
    STAFF {
        @Composable
        override fun localized() = stringResource(R.string.staff)
    },
    STUDIO {
        @Composable
        override fun localized() = stringResource(R.string.studios)
    },
    USER {
        @Composable
        override fun localized() = stringResource(R.string.users)
    },
}