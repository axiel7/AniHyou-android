package com.axiel7.anihyou.wear.ui.screens.main

import android.net.Uri

interface MainEvent {
    fun onIntentDataReceived(data: Uri?)
}