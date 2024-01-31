package com.axiel7.anihyou.ui.screens.explore.search.genretag

import com.axiel7.anihyou.R
import com.axiel7.anihyou.ui.common.TabRowItem

enum class GenresTagsSheetTab {
    GENRES, TAGS;

    companion object {
        val tabRows = arrayOf(
            TabRowItem(value = GENRES, title = R.string.genres),
            TabRowItem(value = TAGS, title = R.string.tags),
        )
    }
}