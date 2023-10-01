package com.axiel7.anihyou.data.model.activity

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.axiel7.anihyou.fragment.ListActivityFragment

fun ListActivityFragment.text(): String {
    return if (progress != null) "${status?.capitalize(Locale.current)} $progress of ${media?.title?.userPreferred}"
    else "${status?.capitalize(Locale.current)} ${media?.title?.userPreferred}"
}

fun ListActivityFragment.updateLikeStatus(isLiked: Boolean) = copy(
    isLiked = isLiked,
    likeCount = if (isLiked) likeCount + 1 else likeCount - 1
)