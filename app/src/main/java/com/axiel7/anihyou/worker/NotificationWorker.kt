package com.axiel7.anihyou.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.TaskStackBuilder
import androidx.datastore.preferences.core.edit
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.axiel7.anihyou.App
import com.axiel7.anihyou.data.PreferencesDataStore.LAST_NOTIFICATION_CREATED_AT_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.defaultPreferencesDataStore
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.model.notification.GenericNotification
import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.data.repository.NotificationRepository
import com.axiel7.anihyou.data.repository.PagedResult
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.NotificationType
import com.axiel7.anihyou.ui.screens.main.MainActivity
import com.axiel7.anihyou.utils.NotificationUtils.createNotificationChannel
import com.axiel7.anihyou.utils.NotificationUtils.showNotification
import kotlinx.coroutines.runBlocking

class NotificationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    // AniList API does not have a socket for notifications, so we schedule a work with an interval
    // chosen by the user and check for new notifications
    override suspend fun doWork(): Result {
        if (isStopped) return Result.success()

        // check first the unread count so we can skip early if there aren't unread notifications
        // e.g.: the user read the notifications on web
        var unreadCount = 0
        runBlocking {
            UserRepository.getUnreadNotificationCount().collect {
                unreadCount = it
            }
        }
        if (unreadCount == 0) return Result.success()

        var notifications: List<GenericNotification>? = null
        runBlocking {
            NotificationRepository.getNotificationsPage(
                type = NotificationTypeGroup.ALL,
                resetCount = false
            ).collect { result ->
                when (result) {
                    is PagedResult.Error -> notifications = null
                    is PagedResult.Success -> notifications = result.data
                    else -> {}
                }
            }
        }
        return if (notifications == null) Result.failure()
        else {
            // since AniList API does not have a filter for createdAt we need to filter
            // locally the new notifications by saving the latest createdAt to preferences
            val lastCreatedAt = applicationContext.defaultPreferencesDataStore
                .getValueSync(LAST_NOTIFICATION_CREATED_AT_PREFERENCE_KEY) ?: 0
            val newNotifications = notifications?.filter {
                it.createdAt != null && it.createdAt > lastCreatedAt
            }
            if (!newNotifications.isNullOrEmpty()) {
                applicationContext.defaultPreferencesDataStore.edit {
                    it[LAST_NOTIFICATION_CREATED_AT_PREFERENCE_KEY] =
                        newNotifications.first().createdAt!!
                }
                newNotifications.forEach {
                    var pendingIntent: PendingIntent? = null
                    // if the notification contains a media, open details on click
                    // TODO: handle user, activity and thread
                    if (it.type == NotificationType.AIRING
                        || NotificationTypeGroup.MEDIA.values.contains(it.type)
                    ) {
                        pendingIntent = TaskStackBuilder.create(applicationContext).run {
                            addNextIntentWithParentStack(
                                Intent(applicationContext, MainActivity::class.java).apply {
                                    action = "media_details"
                                    putExtra("media_id", it.contentId)
                                }
                            )
                            getPendingIntent(
                                0,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        }
                    }
                    applicationContext.showNotification(
                        notificationId = it.id,
                        channelId = DEFAULT_CHANNEL_ID,
                        title = it.text,
                        text = "",
                        pendingIntent = pendingIntent,
                        group = it.type?.name
                    )
                }
            }

            Result.success()
        }
    }

    companion object {
        const val DEFAULT_CHANNEL_ID = "default_channel_id"

        fun scheduleNotificationWork(
            interval: NotificationInterval
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // TODO: create different channels for every NotificationType?
                App.applicationContext.createNotificationChannel(
                    id = DEFAULT_CHANNEL_ID,
                    name = "Default"
                )
            }
            val workManager = WorkManager.getInstance(App.applicationContext)

            val notificationWorkRequest =
                PeriodicWorkRequestBuilder<NotificationWorker>(interval.value, interval.timeUnit)
                    .build()

            workManager.enqueueUniquePeriodicWork(
                "default_notifications",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                notificationWorkRequest
            )
        }

        fun cancelNotificationWork() {
            WorkManager.getInstance(App.applicationContext)
                .cancelUniqueWork("default_notifications")
        }
    }
}