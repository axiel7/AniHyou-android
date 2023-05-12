package com.axiel7.anihyou.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ContextUtils {

    fun Context.showToast(message: String?) =
        message?.let { Toast.makeText(this, message, Toast.LENGTH_SHORT) }

    fun Context.openActionView(url: String) {
        try {
            Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                startActivity(this)
            }
        } catch (e: ActivityNotFoundException) {
            showToast("No app found for this action")
        }
    }
}