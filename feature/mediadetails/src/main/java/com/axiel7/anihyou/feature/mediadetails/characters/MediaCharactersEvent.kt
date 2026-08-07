package com.axiel7.anihyou.feature.mediadetails.characters

import androidx.compose.runtime.Stable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Stable
interface MediaCharactersEvent : UiEvent, PagedEvent {
    fun onLanguageSelect(language: String)
}