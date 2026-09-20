package com.axiel7.anihyou.feature.staffdetails

import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.common.TabRowItem
import kotlinx.collections.immutable.persistentListOf

enum class StaffInfoType {
    INFO, MEDIA, CHARACTER;

    companion object {
        val tabRows = persistentListOf(
            TabRowItem(INFO, icon = R.drawable.info_24),
            TabRowItem(MEDIA, icon = R.drawable.movie_24),
            TabRowItem(CHARACTER, icon = R.drawable.record_voice_over_24),
        )
    }
}