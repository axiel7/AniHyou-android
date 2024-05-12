package com.axiel7.anihyou.ui.widget.medialist

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.LocalContext
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.axiel7.anihyou.MediaListWidgetQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.data.repository.MediaListRepository
import com.axiel7.anihyou.type.MediaType
import com.axiel7.anihyou.ui.screens.main.MainActivity
import com.axiel7.anihyou.ui.theme.AppWidgetColumn
import com.axiel7.anihyou.ui.theme.glanceStringResource
import com.axiel7.anihyou.ui.widget.common.AiringText
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first

class MediaListWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface MediaListWidgetEntryPoint {
        val defaultPreferencesRepository: DefaultPreferencesRepository
        val mediaListRepository: MediaListRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(appContext, MediaListWidgetEntryPoint::class.java)

        val mediaList = getMediaList(hiltEntryPoint)

        provideContent {
            GlanceTheme {
                if (mediaList.isNullOrEmpty()) {
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
                    }
                } else {
                    AppWidgetColumn {
                        LazyColumn {
                            items(mediaList) { item ->
                                ItemView(item = item)
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ItemView(item: MediaListWidgetQuery.MediaList) {
        Row(
            modifier = GlanceModifier
                .padding(bottom = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = GlanceModifier
                    .defaultWeight()
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
                        color = GlanceTheme.colors.onSurface
                    ),
                    maxLines = 1
                )

                Row {
                    val progress = item.media?.mediaListEntry?.progress ?: 0
                    val totalProgress = item.media?.episodes ?: item.media?.chapters
                    Text(
                        text = if (totalProgress != null) "$progress/$totalProgress" else "$progress",
                        modifier = GlanceModifier.padding(end = 8.dp),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurfaceVariant
                        ),
                        maxLines = 1
                    )

                    item.media?.nextAiringEpisode?.let { nextAiringEpisode ->
                        AiringText(schedule = nextAiringEpisode.commonAiringSchedule)
                    }
                }
            }//: Column

            Button(
                text = "+1",
                onClick = actionRunCallback<UpdateProgressAction>(
                    actionParametersOf(
                        UpdateProgressAction.entryIdKey to (item.media?.mediaListEntry?.id ?: 0),
                        UpdateProgressAction.progressKey to (item.media?.mediaListEntry?.progress
                            ?.plus(1) ?: 1)
                    )
                )
            )
        }//: Row
    }

    private suspend fun getMediaList(
        hiltEntryPoint: MediaListWidgetEntryPoint
    ): List<MediaListWidgetQuery.MediaList>? {
        return try {
            val userId = hiltEntryPoint.defaultPreferencesRepository.userId.first()!!
            hiltEntryPoint.mediaListRepository.getMediaListWidgetData(
                userId = userId,
                mediaType = MediaType.ANIME,
                page = 1,
                perPage = 25
            )
        } catch (e: Exception) {
            null
        }
    }
}

class UpdateProgressAction : ActionCallback {

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val entryId = parameters[entryIdKey] ?: return
        val progress = parameters[progressKey] ?: return

        val hiltEntryPoint =
            EntryPointAccessors.fromApplication(
                context,
                MediaListWidget.MediaListWidgetEntryPoint::class.java
            )

        hiltEntryPoint.mediaListRepository.updateEntryProgress(
            entryId = entryId,
            progress = progress,
        ).collect()

        MediaListWidget().updateAll(context)
    }

    companion object {
        val entryIdKey = ActionParameters.Key<Int>("entryId")
        val progressKey = ActionParameters.Key<Int>("progress")
    }
}

class MediaListWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MediaListWidget()
}