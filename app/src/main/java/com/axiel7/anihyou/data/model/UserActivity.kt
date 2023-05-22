package com.axiel7.anihyou.data.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.axiel7.anihyou.UserActivityQuery

@Composable
fun UserActivityQuery.OnListActivity.text(): String {
    return if (progress != null) "${status?.capitalize(Locale.current)} $progress of ${media?.title?.userPreferred}"
    else "${status?.capitalize(Locale.current)} ${media?.title?.userPreferred}"
}