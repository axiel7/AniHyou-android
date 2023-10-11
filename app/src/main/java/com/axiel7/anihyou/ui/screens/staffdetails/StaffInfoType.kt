package com.axiel7.anihyou.ui.screens.staffdetails

import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.TabRowItem

enum class StaffInfoType {
    INFO, MEDIA, CHARACTER;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(INFO, icon = R.drawable.info_24),
            TabRowItem(MEDIA, icon = R.drawable.movie_24),
            TabRowItem(CHARACTER, icon = R.drawable.record_voice_over_24),
        )
    }
}