package com.axiel7.anihyou.feature.worker

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.axiel7.anihyou.core.base.APP_PACKAGE_NAME
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.NotificationRepository
import com.axiel7.anihyou.core.domain.repository.UserRepository
import com.axiel7.anihyou.core.model.notification.NotificationInterval
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup.Companion.asDeepLinkType
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup.Companion.asGroup
import com.axiel7.anihyou.core.network.NetworkVariables
import com.axiel7.anihyou.core.network.type.NotificationType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.utils.ImageUtils.getBitmapFromUrl
import com.axiel7.anihyou.core.ui.utils.NotificationUtils.createNotificationChannel
import com.axiel7.anihyou.core.ui.utils.NotificationUtils.showNotification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
    private val userRepository: UserRepository,
    private val notificationsRepository: NotificationRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val networkVariables: NetworkVariables,
) : CoroutineWorker(context, params) {

    // AniList API does not have a socket for notifications, so we schedule a work with an interval
    // chosen by the user and check for new notifications
    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        try {
            setForegroundSafely()
            val accessToken = defaultPreferencesRepository.accessToken.firstOrNull()
                ?: return Result.failure()
            networkVariables.accessToken = accessToken
            // check first the unread count so we can skip early if there aren't unread notifications
            // e.g.: the user read the notifications on web
            val unreadCount = userRepository.getUnreadNotificationCount().firstOrNull()
                ?: return Result.failure()
            if (unreadCount <= 0) return Result.success()

            val result = notificationsRepository.getNewNotifications(unreadCount)

            return if (result is DataResult.Success && result.data != null) {
                // since AniList API does not have a filter for createdAt we need to filter
                // locally the new notifications by saving the latest createdAt to preferences
                // so we don't notify the same notification more than once
                val lastCreatedAt = defaultPreferencesRepository.lastNotificationCreatedAt
                    .firstOrNull() ?: 0
                val newNotifications = result.data!!.filter {
                    it.createdAt != null && it.createdAt!! > lastCreatedAt
                }
                if (newNotifications.isNotEmpty()) {
                    newNotifications.firstOrNull()?.createdAt?.let { createdAt ->
                        defaultPreferencesRepository.setLastNotificationCreatedAt(createdAt)
                    }
                }
                newNotifications.groupBy { it.type }.forEach { (type, notifications) ->
                    val group = type?.asGroup() ?: NotificationTypeGroup.ALL
                    notifications.forEach {
                        var pendingIntent: PendingIntent? = null
                        val deepLinkType = group.asDeepLinkType()
                        if (deepLinkType != null) {
                            applicationContext.packageManager
                                .getLaunchIntentForPackage(APP_PACKAGE_NAME)
                                ?.apply {
                                    action = deepLinkType.intentAction
                                    putExtra("content_id", it.contentId)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                            Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }?.let { intent ->
                                    pendingIntent = PendingIntent.getActivity(
                                        applicationContext, it.id, intent,
                                        PendingIntent.FLAG_IMMUTABLE
                                    )
                                }
                        }

                        val image = (it.largeImageUrl ?: it.imageUrl)?.let { url ->
                            applicationContext.getBitmapFromUrl(url)
                        }

                        val title = if (it.type == NotificationType.AIRING) {
                            it.mediaTitle() ?: it.text
                        } else it.text

                        val text = if (it.type == NotificationType.AIRING) {
                            it.numEpisode()?.let { ep -> "Episode $ep aired" }.orEmpty()
                        } else ""

                        applicationContext.showNotification(
                            notificationId = it.id,
                            channelId = group.channelId,
                            title = title,
                            text = text,
                            largeIcon = image,
                            bigPicture = image.takeIf { _ -> it.isMedia },
                            pendingIntent = pendingIntent,
                            group = group.name
                        )
                    }
                    if (notifications.size > 1) {
                        applicationContext.showNotification(
                            notificationId = 1,
                            channelId = group.channelId,
                            title = "${applicationContext.getString(group.stringRes)} (${newNotifications.size})",
                            text = "",
                            group = group.name,
                            isGroupSummary = true
                        )
                    }
                }
                Result.success()
            } else Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: ", e)
            return Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            0,
            NotificationCompat.Builder(applicationContext, SYNC_CHANNEL_ID)
                .setContentTitle(applicationContext.getString(R.string.notifications))
                .setSmallIcon(R.drawable.anihyou_24)
                .setAutoCancel(true)
                .build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            }
        )
    }

    private suspend fun setForegroundSafely() {
        try {
            setForeground(getForegroundInfo())
            delay(500.milliseconds)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "setForegroundSafely: ", e)
        }
    }

    companion object {
        private const val TAG = "NotificationWorker"
        private const val WORK_NAME = "default_notifications"

        const val DEFAULT_CHANNEL_ID = "default_channel_id"
        const val AIRING_CHANNEL_ID = "airing_channel_id"
        const val ACTIVITY_CHANNEL_ID = "activity_channel_id"
        const val FORUM_CHANNEL_ID = "forum_channel_id"
        const val FOLLOWING_CHANNEL_ID = "following_channel_id"
        const val MEDIA_CHANNEL_ID = "media_channel_id"
        const val SUBMISSION_CHANNEL_ID = "submission_channel_id"
        const val SYNC_CHANNEL_ID = "sync_channel_id"

        val NotificationTypeGroup.channelId
            get() = when (this) {
                NotificationTypeGroup.ALL -> DEFAULT_CHANNEL_ID
                NotificationTypeGroup.AIRING -> AIRING_CHANNEL_ID
                NotificationTypeGroup.ACTIVITY -> ACTIVITY_CHANNEL_ID
                NotificationTypeGroup.FORUM -> FORUM_CHANNEL_ID
                NotificationTypeGroup.FOLLOWS -> FOLLOWING_CHANNEL_ID
                NotificationTypeGroup.MEDIA -> MEDIA_CHANNEL_ID
                NotificationTypeGroup.SUBMISSION -> SUBMISSION_CHANNEL_ID
            }

        fun Context.createDefaultNotificationChannels() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationTypeGroup.entries.forEach { type ->
                    val stringRes = if (type == NotificationTypeGroup.ALL) R.string.default_setting
                    else type.stringRes
                    createNotificationChannel(
                        id = type.channelId,
                        name = getString(stringRes)
                    )
                }
                createNotificationChannel(
                    id = SYNC_CHANNEL_ID,
                    name = getString(R.string.update_interval)
                )
            }
        }

        fun WorkManager.scheduleNotificationWork(
            interval: NotificationInterval
        ) {
            val notificationWorkRequest =
                PeriodicWorkRequestBuilder<NotificationWorker>(
                    repeatInterval = interval.value,
                    repeatIntervalTimeUnit = interval.timeUnit,
                    flexTimeInterval = 1,
                    flexTimeIntervalUnit = TimeUnit.HOURS
                ).apply {
                    addTag(WORK_NAME)
                    setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                    setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                }.build()

            enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                notificationWorkRequest
            )
        }

        fun WorkManager.cancelNotificationWork() {
            cancelUniqueWork(WORK_NAME)
        }
    }
}