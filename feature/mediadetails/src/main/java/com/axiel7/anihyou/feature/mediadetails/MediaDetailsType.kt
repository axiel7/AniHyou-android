package com.axiel7.anihyou.feature.mediadetails

import com.axiel7.anihyou.core.ui.common.TabRowItem
import com.axiel7.anihyou.core.resources.R

enum class MediaDetailsType {
    INFO, EPISODES, STAFF_CHARACTERS, RELATIONS, STATS, REVIEWS;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(INFO, icon = R.drawable.info_24),
            TabRowItem(EPISODES, icon = R.drawable.play_circle_24),
            TabRowItem(STAFF_CHARACTERS, icon = R.drawable.group_24),
            TabRowItem(RELATIONS, icon = R.drawable.shuffle_24),
            TabRowItem(STATS, icon = R.drawable.bar_chart_24),
            TabRowItem(REVIEWS, icon = R.drawable.rate_review_24),
        )
    }
}
