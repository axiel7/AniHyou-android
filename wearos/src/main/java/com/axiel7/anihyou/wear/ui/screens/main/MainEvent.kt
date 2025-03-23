package com.axiel7.anihyou.wear.ui.screens.main

import android.content.Context
import android.net.Uri

interface MainEvent {
    fun launchLoginIntent(context: Context)
    fun onIntentDataReceived(data: Uri?)
}