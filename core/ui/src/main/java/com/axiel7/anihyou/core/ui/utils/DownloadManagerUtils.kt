package com.axiel7.anihyou.core.ui.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri
import com.axiel7.anihyou.core.resources.R

fun Context.downloadImage(url: String) {
    if (url.isBlank()) return

    val request = DownloadManager.Request(url.toUri()).apply {
        setTitle(getString(R.string.image_download_title))
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val fileName = url.substringAfterLast('/').ifBlank { "image_${System.currentTimeMillis()}.jpg" }
        setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "AniHyou/$fileName")
    }

    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
    Toast.makeText(this, getString(R.string.image_downloading), Toast.LENGTH_SHORT).show()
}