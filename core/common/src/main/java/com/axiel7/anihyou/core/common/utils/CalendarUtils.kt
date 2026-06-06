package com.axiel7.anihyou.core.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import com.axiel7.anihyou.core.base.GOOGLE_CALENDAR_PACKAGE
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Google Calendar integration utilities.
 *
 * Provides two levels of integration:
 *  1. [insertAiringEvent] — Opens the Google Calendar "Add Event" UI pre-filled.
 *     No permissions required. User confirms before saving.
 *
 *  2. [buildCalendarQueryIntent] — Opens Calendar on the date of an airing episode
 *     so the user can view their existing events alongside the airing schedule.
 *
 * For full read/write calendar integration (background sync without opening Calendar)
 * you'd need READ_CALENDAR + WRITE_CALENDAR permissions and the Calendar ContentProvider.
 * The intent approach used here requires zero extra permissions and is the recommended
 * pattern for "add to calendar" from a third-party app.
 */
object CalendarUtils {

    /**
     * Open Google Calendar's "New Event" screen pre-filled with an airing episode.
     *
     * @param context  Any context.
     * @param title    Event title, e.g. "Jujutsu Kaisen — Episode 24 Airs"
     * @param startEpochSeconds  Unix timestamp of the airing time.
     * @param description  Optional description shown in the event body.
     * @param durationMinutes  Event duration in minutes (default 25 for a half-hour episode).
     */
    fun Context.insertAiringEvent(
        title: String,
        startEpochSeconds: Long,
        description: String = "",
        durationMinutes: Int = 25,
    ) {
        val startMillis = startEpochSeconds * 1000L
        val endMillis = startMillis + (durationMinutes * 60 * 1000L)

        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, title)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)
            // Suggest a reminder 30 minutes before
            putExtra(CalendarContract.Reminders.MINUTES, 30)
            putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }

        // Try Google Calendar first, fall back to any calendar app
        val resolvedActivities = packageManager
            .queryIntentActivities(intent, 0)
        val googleCalendar = resolvedActivities.firstOrNull {
            it.activityInfo.packageName == GOOGLE_CALENDAR_PACKAGE
        }

        if (googleCalendar != null) {
            intent.setPackage(GOOGLE_CALENDAR_PACKAGE)
        }

        if (resolvedActivities.isNotEmpty()) {
            startActivity(intent)
        }
    }

    /**
     * Open Google Calendar on the date that a dubbed episode airs.
     * Useful for "View in Calendar" action from the dub schedule screen.
     *
     * @param dubAirDate  "YYYY-MM-DD" string from TheTVDB.
     */
    fun Context.openCalendarOnDate(dubAirDate: String) {
        runCatching {
            val date = LocalDate.parse(dubAirDate)
            val epochMillis = LocalDateTime.of(date, LocalTime.NOON)
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            val uri = Uri.parse("content://com.android.calendar/time/$epochMillis")
            val intent = Intent(Intent.ACTION_VIEW, uri).apply {
                putExtra("time", epochMillis)
            }
            startActivity(intent)
        }
    }

    /**
     * Build a batch of calendar insert intents for an entire season's dub schedule.
     * Returns the list so the caller can present a confirmation UI before firing them.
     *
     * @param seriesTitle  The anime title.
     * @param episodes     List of (episodeNumber, dubAirDateString "YYYY-MM-DD") pairs.
     */
    fun buildSeasonCalendarIntents(
        seriesTitle: String,
        seasonNumber: Int,
        episodes: List<Pair<Int, String>>, // episodeNumber to "YYYY-MM-DD"
    ): List<Intent> = episodes.mapNotNull { (epNum, dateStr) ->
        runCatching {
            val date = LocalDate.parse(dateStr)
            val startMillis = LocalDateTime.of(date, LocalTime.of(20, 0))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
            val endMillis = startMillis + 25 * 60 * 1000L

            Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(
                    CalendarContract.Events.TITLE,
                    "$seriesTitle S${seasonNumber}E${epNum} (Dub)"
                )
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endMillis)
                putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    "English dub of $seriesTitle Season $seasonNumber Episode $epNum"
                )
                putExtra(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)
                putExtra(CalendarContract.Reminders.MINUTES, 30)
                putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
            }
        }.getOrNull()
    }
}
