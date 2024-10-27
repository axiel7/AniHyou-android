package com.axiel7.anihyou.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.axiel7.anihyou.R

object NotificationUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun Context.createNotificationChannel(
        id: String,
        name: String,
        description: String = "",
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        val channel = NotificationChannel(id, name, importance)
            .apply { this.description = description }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .apply { createNotificationChannel(channel) }
    }

    fun Context.showNotification(
        notificationId: Int,
        channelId: String,
        title: String,
        text: String,
        largeIcon: Bitmap? = null,
        bigPicture: Bitmap? = null,
        pendingIntent: PendingIntent? = null,
        group: String? = null,
        isGroupSummary: Boolean = false,
    ): Boolean {
        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.anihyou_24)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setGroup(group)
            .setGroupSummary(isGroupSummary)
            .setLargeIcon(largeIcon)

        bigPicture?.let {
            builder.setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(bigPicture)
                .bigLargeIcon(null as Bitmap?)
            )
        }

        with(NotificationManagerCompat.from(applicationContext)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
            notify(notificationId, builder.build())
            return true
        }
    }
}