package com.axiel7.anihyou.core.ui.utils

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.core.net.toUri

fun Context.downloadImage(url: String, data: Triple<String, String, String>) {
    if (url.isBlank()) return

    val request = DownloadManager.Request(url.toUri()).apply {
        setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        setTitle(data.first)
        setDescription(data.second)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val fileName = url.substringAfterLast('/').ifBlank { "image_${System.currentTimeMillis()}.jpg" }
        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
    }

    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    downloadManager.enqueue(request)
    Toast.makeText(this, data.third, Toast.LENGTH_SHORT).show()
}