package com.axiel7.anihyou.feature.settings.customlinks

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent
import com.axiel7.anihyou.core.network.type.MediaType

@Immutable
interface CustomLinksEvent : UiEvent {
    fun onLinkAdded(link: String, mediaType: MediaType)
    fun onLinkRemoved(link: String, mediaType: MediaType)
    fun onLinkEdited(prev: String, new: String, mediaType: MediaType)
}