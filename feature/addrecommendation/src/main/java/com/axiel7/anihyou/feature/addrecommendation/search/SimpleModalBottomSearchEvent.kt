package com.axiel7.anihyou.feature.addrecommendation.search

import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.type.MediaType

interface SimpleModalBottomSearchEvent : UiEvent {
    fun setQuery(query: String)
    fun search(type: MediaType)
}