package com.axiel7.anihyou.feature.profile.social

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface UserSocialEvent : UiEvent, PagedEvent {
    fun setType(value: UserSocialType)
}