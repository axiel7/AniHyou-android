package com.axiel7.anihyou.wear.ui.screens.login

import android.content.Context
import com.axiel7.anihyou.core.base.event.UiEvent

interface LoginEvent : UiEvent {
    fun launchLoginIntent(context: Context)
}