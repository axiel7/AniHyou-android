package com.axiel7.anihyou.feature.addrecommendation.search

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface SimpleMediaSearchEvent : UiEvent {
    fun setQuery(query: String)
    fun search()
}