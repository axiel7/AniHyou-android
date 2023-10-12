package com.axiel7.anihyou.ui.screens.mediadetails

import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.TabRowItem

enum class MediaDetailsType {
    INFO, STAFF_CHARACTERS, RELATIONS, STATS, REVIEWS;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(INFO, icon = R.drawable.info_24),
            TabRowItem(STAFF_CHARACTERS, icon = R.drawable.group_24),
            TabRowItem(RELATIONS, icon = R.drawable.shuffle_24),
            TabRowItem(STATS, icon = R.drawable.bar_chart_24),
            TabRowItem(REVIEWS, icon = R.drawable.rate_review_24)
        )
    }
}