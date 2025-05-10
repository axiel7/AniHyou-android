package com.axiel7.anihyou.core.common.utils

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.axiel7.anihyou.core.base.APP_PACKAGE_NAME
import com.axiel7.anihyou.core.resources.R

object ContextUtils {

    fun Context.showToast(message: String?) =
        message?.let { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }

    fun Context.showToast(@StringRes stringRes: Int) = showToast(getString(stringRes))

    fun Context.openActionView(url: String) {
        openActionView(url.toUri())
    }

    fun Context.openActionView(uri: Uri) {
        try {
            Intent(Intent.ACTION_VIEW, uri).apply {
                startActivity(this)
            }
        } catch (_: ActivityNotFoundException) {
            showToast(getString(R.string.no_app_found_for_this_action))
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun Context.openByDefaultSettings() {
        try {
            // Samsung OneUI 4 bug can't open ACTION_APP_OPEN_BY_DEFAULT_SETTINGS
            val action = if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            } else {
                Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS
            }
            Intent(
                action,
                "package:${packageName}".toUri()
            ).apply {
                startActivity(this)
            }
        } catch (e: Exception) {
            showToast(e.message)
        }
    }

    fun Context.openShareSheet(text: String) {
        Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            startActivity(Intent.createChooser(this, null))
        }
    }

    /** Open external link by default browser or intent chooser */
    fun Context.openLink(url: String) {
        val uri = url.toUri()
        Intent(Intent.ACTION_VIEW, uri).apply {
            val defaultBrowser =
                findBrowserIntentActivities(PackageManager.MATCH_DEFAULT_ONLY).firstOrNull()
            if (defaultBrowser != null && defaultBrowser.isDefault) {
                try {
                    setPackage(defaultBrowser.activityInfo.packageName)
                    startActivity(this)
                } catch (_: ActivityNotFoundException) {
                    startActivity(Intent.createChooser(this, null))
                }
            } else {
                val browsers = findBrowserIntentActivities(PackageManager.MATCH_ALL)
                val intents = browsers.map {
                    Intent(Intent.ACTION_VIEW, uri).apply {
                        setPackage(it.activityInfo.packageName)
                    }
                }
                startActivity(
                    Intent.createChooser(this, null).apply {
                        putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray())
                    }
                )
            }
        }
    }

    /** Finds all the browsers installed on the device */
    private fun Context.findBrowserIntentActivities(
        flags: Int = 0
    ): List<ResolveInfo> {
        val emptyBrowserIntent = Intent(Intent.ACTION_VIEW, Uri.fromParts("http", "", null))

        return packageManager
            .queryIntentActivitiesCompat(emptyBrowserIntent, flags)
            .filter { it.activityInfo.packageName != APP_PACKAGE_NAME }
            .sortedBy { it.priority }
    }

    /** Custom compat method until Google decides to make one */
    private fun PackageManager.queryIntentActivitiesCompat(intent: Intent, flags: Int = 0) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(flags.toLong()))
        } else {
            queryIntentActivities(intent, flags)
        }

    fun Context.getActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.getActivity()
        else -> null
    }

    fun Context.copyToClipBoard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText("title", text))
        // Android 13+ has clipboard popups
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            showToast(getString(R.string.copied))
        }
    }
}