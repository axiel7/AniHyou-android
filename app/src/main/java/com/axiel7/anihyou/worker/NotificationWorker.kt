package com.axiel7.anihyou.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.TaskStackBuilder
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.axiel7.anihyou.data.api.NotificationsApi
import com.axiel7.anihyou.data.model.notification.GenericNotification.Companion.toGenericNotifications
import com.axiel7.anihyou.data.model.notification.NotificationInterval
import com.axiel7.anihyou.data.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.UserRepository
import com.axiel7.anihyou.type.NotificationType
import com.axiel7.anihyou.ui.screens.main.MainActivity
import com.axiel7.anihyou.utils.NotificationUtils.createNotificationChannel
import com.axiel7.anihyou.utils.NotificationUtils.showNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val userRepository: UserRepository,
    private val notificationsApi: NotificationsApi,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
) : CoroutineWorker(context, params) {

    // AniList API does not have a socket for notifications, so we schedule a work with an interval
    // chosen by the user and check for new notifications
    override suspend fun doWork(): Result {
        if (isStopped) return Result.success()

        // check first the unread count so we can skip early if there aren't unread notifications
        // e.g.: the user read the notifications on web
        val unreadCount = userRepository.getUnreadNotificationCount().firstOrNull()
        if (unreadCount == 0) return Result.success()

        val notifications = notificationsApi.notificationsQuery(
            typeIn = null,
            resetCount = false,
            page = 1,
            perPage = 25
        ).execute().data?.Page?.notifications?.filterNotNull()?.toGenericNotifications()

        return if (notifications == null) Result.failure()
        else {
            // since AniList API does not have a filter for createdAt we need to filter
            // locally the new notifications by saving the latest createdAt to preferences
            val lastCreatedAt = defaultPreferencesRepository.lastNotificationCreatedAt
                .firstOrNull() ?: 0
            val newNotifications = notifications.filter {
                it.createdAt != null && it.createdAt > lastCreatedAt
            }
            if (newNotifications.isNotEmpty()) {
                newNotifications.firstOrNull()?.createdAt?.let { createdAt ->
                    defaultPreferencesRepository.setLastNotificationCreatedAt(createdAt)
                }
                newNotifications.forEach {
                    var pendingIntent: PendingIntent? = null
                    // if the notification contains a media, open details on click
                    // TODO: handle user, activity and thread
                    if (it.type == NotificationType.AIRING
                        || NotificationTypeGroup.MEDIA.values?.contains(it.type) == true
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

        fun Context.createDefaultNotificationChannels() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // TODO: create different channels for every NotificationType?
                createNotificationChannel(
                    id = DEFAULT_CHANNEL_ID,
                    name = "Default"
                )
            }
        }

        fun WorkManager.scheduleNotificationWork(
            interval: NotificationInterval
        ) {
            val notificationWorkRequest =
                PeriodicWorkRequestBuilder<NotificationWorker>(interval.value, interval.timeUnit)
                    .build()

            enqueueUniquePeriodicWork(
                "default_notifications",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
                notificationWorkRequest
            )
        }

        fun WorkManager.cancelNotificationWork() {
            cancelUniqueWork("default_notifications")
        }
    }
}