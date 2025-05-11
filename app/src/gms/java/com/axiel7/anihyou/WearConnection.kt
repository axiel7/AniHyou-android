package com.axiel7.anihyou

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.concurrent.futures.await
import androidx.wear.remote.interactions.RemoteActivityHelper
import com.axiel7.anihyou.core.common.utils.ContextUtils.showToast

suspend fun Context.startRemoteActivity(data: Uri) = try {
    RemoteActivityHelper(this).startRemoteActivity(
        Intent(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(data),
    ).await()
} catch (e: RemoteActivityHelper.RemoteIntentException) {
    showToast(e.message)
}