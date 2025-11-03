package com.axiel7.anihyou.wear.ui.screens.login

import android.content.Context
import androidx.compose.runtime.Immutable
import com.axiel7.anihyou.core.base.event.UiEvent

@Immutable
interface LoginEvent : UiEvent {
    fun launchLoginIntent(context: Context)
}