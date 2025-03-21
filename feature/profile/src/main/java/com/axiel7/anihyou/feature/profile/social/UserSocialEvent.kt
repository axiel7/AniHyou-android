package com.axiel7.anihyou.feature.profile.social

import com.axiel7.anihyou.core.ui.common.event.PagedEvent

interface UserSocialEvent : PagedEvent {
    fun setType(value: UserSocialType)
}