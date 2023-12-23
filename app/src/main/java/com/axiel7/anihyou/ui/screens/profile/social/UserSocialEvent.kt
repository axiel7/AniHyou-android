package com.axiel7.anihyou.ui.screens.profile.social

import com.axiel7.anihyou.ui.common.event.PagedEvent

interface UserSocialEvent : PagedEvent {
    fun setType(value: UserSocialType)
}