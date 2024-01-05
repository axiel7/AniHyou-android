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
import com.axiel7.anihyou.R
import com.axiel7.anihyou.UserCurrentAnimeListQuery
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaRepository
import com.axiel7.anihyou.ui.screens.main.MainActivity
import com.axiel7.anihyou.ui.theme.AppWidgetColumn
import com.axiel7.anihyou.ui.theme.glanceStringResource
import com.axiel7.anihyou.utils.DateUtils.currentTimeSeconds
import com.axiel7.anihyou.utils.DateUtils.secondsToLegibleText
import com.axiel7.anihyou.utils.NumberUtils.format
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlin.math.absoluteValue

class AiringWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface AiringWidgetEntryPoint {
        val defaultPreferencesRepository: DefaultPreferencesRepository
        val mediaRepository: MediaRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(appContext, AiringWidgetEntryPoint::class.java)

        val animeList = getAiringAnime(hiltEntryPoint)

        provideContent {
            GlanceTheme {
                if (animeList.isNullOrEmpty()) {
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = glanceStringResource(R.string.no_information),
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
                                            Intent(
                                                LocalContext.current,
                                                MainActivity::class.java
                                            ).apply {
                                                action = "media_details"
                                                putExtra("media_id", item.mediaId)
                                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                addCategory(item.mediaId.toString())
                                            }
                                        ))
                                ) {
                                    Text(
                                        text = item.media?.title?.userPreferred.orEmpty(),
                                        style = TextStyle(
                                            color = GlanceTheme.colors.onSurfaceVariant
                                        ),
                                        maxLines = 1
                                    )

                                    item.media?.nextAiringEpisode?.let { nextAiringEpisode ->
                                        val airingIn =
                                            nextAiringEpisode.airingAt.toLong() - currentTimeSeconds()
                                        val airingText = if (airingIn > 0) {
                                            val timeText = airingIn.secondsToLegibleText(
                                                buildString = { id, time ->
                                                    LocalContext.current.getString(id, time.format())
                                                }
                                            )
                                            LocalContext.current.getString(
                                                R.string.episode_in_time,
                                                nextAiringEpisode.episode,
                                                timeText
                                            )
                                        } else {
                                            val timeText =
                                                airingIn.absoluteValue.secondsToLegibleText(
                                                    buildString = { id, time ->
                                                        LocalContext.current.getString(id, time.format())
                                                    }
                                                )
                                            LocalContext.current.getString(
                                                R.string.episode_aired_ago,
                                                nextAiringEpisode.episode,
                                                timeText
                                            )
                                        }
                                        Text(
                                            text = airingText,
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

    private suspend fun getAiringAnime(
        hiltEntryPoint: AiringWidgetEntryPoint
    ): List<UserCurrentAnimeListQuery.MediaList>? {
        return try {
            val userId = hiltEntryPoint.defaultPreferencesRepository.userId.first()!!
            hiltEntryPoint.mediaRepository.getUserCurrentAnimeAiringList(userId)
        } catch (e: Exception) {
            null
        }
    }
}

class AiringWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AiringWidget()
}