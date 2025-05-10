package com.axiel7.anihyou.feature.profile

import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.TabRowItem

enum class ProfileInfoType {
    ABOUT, ACTIVITY, STATS, FAVORITES, SOCIAL;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(ABOUT, icon = R.drawable.info_24),
            TabRowItem(ACTIVITY, icon = R.drawable.forum_24),
            TabRowItem(STATS, icon = R.drawable.bar_chart_24),
            TabRowItem(FAVORITES, icon = R.drawable.star_24),
            TabRowItem(SOCIAL, icon = R.drawable.group_24)
        )
    }
}