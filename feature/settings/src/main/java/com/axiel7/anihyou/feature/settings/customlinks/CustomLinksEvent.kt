package com.axiel7.anihyou.feature.settings.customlinks

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.domain.model.CustomLink

@Immutable
interface CustomLinksEvent : UiEvent {
    fun onLinkAdded(link: CustomLink)
    fun onLinkRemoved(link: CustomLink)
}