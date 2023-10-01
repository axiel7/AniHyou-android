package com.axiel7.anihyou.data.model.activity

import com.axiel7.anihyou.fragment.TextActivityFragment

fun TextActivityFragment.updateLikeStatus(isLiked: Boolean) = copy(
    isLiked = isLiked,
    likeCount = if (isLiked) likeCount + 1 else likeCount - 1
)