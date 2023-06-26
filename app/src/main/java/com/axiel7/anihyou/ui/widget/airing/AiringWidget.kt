package com.axiel7.anihyou.ui.widget.airing

import android.content.Context
import android.content.Intent
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.axiel7.anihyou.ui.screens.MainActivity
import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserCurrentAnimeListQuery
import com.axiel7.anihyou.data.PreferencesDataStore.USER_ID_PREFERENCE_KEY
import com.axiel7.anihyou.data.PreferencesDataStore.defaultPreferencesDataStore
import com.axiel7.anihyou.data.PreferencesDataStore.getValueSync
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.ui.theme.AppWidgetColumn
import com.axiel7.anihyou.ui.theme.stringResource
import com.axiel7.anihyou.utils.DateUtils.secondsToLocalDateTime
import com.axiel7.anihyou.utils.DateUtils.toLocalized

class AiringWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val animeList = getAiringAnime(context)

        provideContent {
            GlanceTheme {
                if (animeList.isNullOrEmpty()) {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_information),
                            modifier = GlanceModifier.padding(bottom = 8.dp),
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface
                            )
                        )
                    }//: Column
                } else {
                    AppWidgetColumn {
                        LazyColumn {
                            items(animeList) { item ->
                                Column(
                                    modifier = GlanceModifier
                                        .padding(bottom = 8.dp)
                                        .fillMaxWidth()
                                        .clickable(actionStartActivity(
                                            Intent(LocalContext.current, MainActivity::class.java).apply {
                                                action = "media_details"
                                                putExtra("media_id", item.mediaId)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                addCategory(item.mediaId.toString())
                                            }
                                        ))
                                ) {
                                    Text(
                                        text = item.media?.title?.userPreferred ?: "",
                                        style = TextStyle(
                                            color = GlanceTheme.colors.onSurfaceVariant
                                        ),
                                        maxLines = 1
                                    )

                                    item.media?.nextAiringEpisode?.let { nextAiringEpisode ->
                                        val airingAt = nextAiringEpisode.airingAt.toLong()
                                            .secondsToLocalDateTime()?.toLocalized() ?: "-"
                                        Text(
                                            text = LocalContext.current.getString(R.string.episode_airing_on,
                                                nextAiringEpisode.episode,
                                                airingAt
                                            ),
                                            style = TextStyle(
                                                color = GlanceTheme.colors.onPrimaryContainer
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }//: LazyColumn
                    }//: Column
                }
            }
        }
    }
}

suspend fun getAiringAnime(context: Context): List<UserCurrentAnimeListQuery.MediaList>? {
    return try {
        val userId = context.defaultPreferencesDataStore.getValueSync(USER_ID_PREFERENCE_KEY)!!
        MediaRepository.getUserCurrentAiringAnime(userId)
    } catch (e: Exception) {
        null
    }
}

class AiringWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AiringWidget()
}