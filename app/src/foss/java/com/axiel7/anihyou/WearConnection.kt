package com.axiel7.anihyou

import android.content.Context
import android.net.Uri
import com.axiel7.anihyou.core.common.utils.ContextUtils.showToast

suspend fun Context.startRemoteActivity(data: Uri) {
    showToast("This feature is only available in the Play Store app version")
}