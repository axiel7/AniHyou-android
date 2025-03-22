package com.axiel7.anihyou.feature.profile.social

import com.axiel7.anihyou.core.base.event.PagedEvent

interface UserSocialEvent : PagedEvent {
    fun setType(value: UserSocialType)
}