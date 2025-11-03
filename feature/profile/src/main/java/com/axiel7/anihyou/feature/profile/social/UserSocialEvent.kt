package com.axiel7.anihyou.feature.profile.social

import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.PagedEvent

@Immutable
interface UserSocialEvent : PagedEvent {
    fun setType(value: UserSocialType)
}