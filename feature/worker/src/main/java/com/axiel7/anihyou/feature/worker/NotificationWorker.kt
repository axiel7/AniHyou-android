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
import com.axiel7.anihyou.core.model.notification.NotificationTypeGroup
import com.axiel7.anihyou.core.model.notification.NotificationInterval
import com.axiel7.anihyou.core.network.NetworkVariables
import com.axiel7.anihyou.core.network.type.NotificationType
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.utils.ImageUtils.getBitmapFromUrl
import com.axiel7.anihyou.core.ui.utils.NotificationUtils.createNotificationChannel
import com.axiel7.anihyou.core.ui.utils.NotificationUtils.showNotification
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import java.util.concurrent.TimeUnit

class NotificationWorker(
    context: Context,
    params: WorkerParameters,
    private val userRepository: UserRepository,
    private val notificationsRepository: NotificationRepository,
    private val defaultPreferencesRepository: DefaultPreferencesRepository,
    private val networkVariables: NetworkVariables,
    private val streamRepository: com.axiel7.anihyou.feature.stream.data.repository.StreamRepository,
) : CoroutineWorker(context, params) {

    // AniList API does not have a socket for notifications, so we schedule a work with an interval
    // chosen by the user and check for new notifications
    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        try {
            setForegroundSafely()
            
            // Check custom reminders first
            checkCustomReminders()

            val accessToken = defaultPreferencesRepository.accessToken.firstOrNull()
                ?: return Result.success()
            networkVariables.accessToken = accessToken
            // check first the unread count so we can skip early if there aren't unread notifications
            // e.g.: the user read the notifications on web
            val unreadCount = userRepository.getUnreadNotificationCount().firstOrNull()
                ?: return Result.success()
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
                newNotifications.forEach {
                    var pendingIntent: PendingIntent? = null
                    // if the notification contains a media, open details on click
                    // TODO: handle user, activity and thread
                    if (it.type == NotificationType.AIRING
                        || NotificationTypeGroup.MEDIA.values?.contains(it.type) == true
                    ) {
                        val intent = applicationContext.packageManager
                            .getLaunchIntentForPackage(APP_PACKAGE_NAME)
                            ?.apply {
                                action = "media_details"
                                putExtra("media_id", it.contentId)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        pendingIntent = PendingIntent.getActivity(
                            applicationContext, it.id, intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )
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
                        channelId = DEFAULT_CHANNEL_ID,
                        title = title,
                        text = text,
                        largeIcon = image,
                        bigPicture = image.takeIf { _ -> it.isMedia },
                        pendingIntent = pendingIntent,
                        group = "default"
                    )
                }
                if (newNotifications.size > 1) {
                    applicationContext.showNotification(
                        notificationId = 1,
                        channelId = DEFAULT_CHANNEL_ID,
                        title = "${newNotifications.size} ${applicationContext.getString(R.string.notifications)}",
                        text = "",
                        group = "default",
                        isGroupSummary = true
                    )
                }

                Result.success()
            } else Result.retry()
        } catch (e: Exception) {
            Log.e(TAG, "doWork: ", e)
            return Result.retry()
        }
    }

    @RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    private suspend fun checkCustomReminders() {
        val remindersRaw = defaultPreferencesRepository.episodeReminders.firstOrNull() ?: ""
        if (remindersRaw.isBlank()) return

        val reminders = remindersRaw.split(",")
            .filter { it.isNotBlank() }
            .mapNotNull {
                val parts = it.split(":")
                if (parts.size == 2) parts[0].toIntOrNull() to parts[1] else null
            }

        for ((animeId, prefLang) in reminders) {
            if (animeId == null) continue
            val epResult = streamRepository.getEpisodes(animeId)
            if (epResult is DataResult.Success) {
                val epData = epResult.data
                val providerData = epData.providers["kiwi"]
                    ?: epData.providers.values.firstOrNull()

                if (providerData != null) {
                    val subEpisodes = providerData.episodes.sub
                    val dubEpisodes = providerData.episodes.dub

                    val subCount = subEpisodes.size
                    val dubCount = dubEpisodes.size

                    val lastNotified = defaultPreferencesRepository.getLastNotifiedEpisode(animeId).firstOrNull() ?: 0

                    val maxAvailable = when (prefLang) {
                        "sub" -> subCount
                        "dub" -> dubCount
                        "both" -> maxOf(subCount, dubCount)
                        else -> 0
                    }

                    if (maxAvailable > lastNotified) {
                        val infoResult = streamRepository.getAnimeInfo(animeId)
                        val title = if (infoResult is DataResult.Success) {
                            infoResult.data?.displayTitle ?: "Anime update"
                        } else "Anime update"

                        val text = when (prefLang) {
                            "sub" -> "Episode $maxAvailable (SUB) is available!"
                            "dub" -> "Episode $maxAvailable (DUB) is available!"
                            else -> "Episode $maxAvailable is available!"
                        }

                        val intent = applicationContext.packageManager
                            .getLaunchIntentForPackage(APP_PACKAGE_NAME)
                            ?.apply {
                                action = "media_details"
                                putExtra("media_id", animeId)
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                        val pendingIntent = PendingIntent.getActivity(
                            applicationContext, animeId, intent,
                            PendingIntent.FLAG_IMMUTABLE
                        )

                        val largeIcon = if (infoResult is DataResult.Success) {
                            infoResult.data?.coverUrl?.let { url ->
                                runCatching { applicationContext.getBitmapFromUrl(url) }.getOrNull()
                            }
                        } else null

                        applicationContext.showNotification(
                            notificationId = animeId,
                            channelId = DEFAULT_CHANNEL_ID,
                            title = title,
                            text = text,
                            largeIcon = largeIcon,
                            pendingIntent = pendingIntent,
                            group = "reminders"
                        )

                        defaultPreferencesRepository.setLastNotifiedEpisode(animeId, maxAvailable)
                    }
                }
            }
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
            delay(500)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "setForegroundSafely: ", e)
        }
    }

    companion object {
        private const val TAG = "NotificationWorker"
        private const val WORK_NAME = "default_notifications"

        const val DEFAULT_CHANNEL_ID = "default_channel_id"
        const val SYNC_CHANNEL_ID = "sync_channel_id"

        fun Context.createDefaultNotificationChannels() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // TODO: create different channels for every NotificationType?
                createNotificationChannel(
                    id = DEFAULT_CHANNEL_ID,
                    name = getString(R.string.default_setting)
                )
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