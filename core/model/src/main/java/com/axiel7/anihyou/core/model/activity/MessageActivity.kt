package com.axiel7.anihyou.core.model.activity

import com.axiel7.anihyou.core.network.fragment.MessageActivityFragment

fun MessageActivityFragment.updateLikeStatus(isLiked: Boolean) = copy(
    isLiked = isLiked,
    likeCount = if (isLiked) likeCount + 1 else likeCount - 1
)