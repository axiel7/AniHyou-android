package com.axiel7.anihyou.ui.widget.airing

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.axiel7.anihyou.AiringWidgetQuery
import com.axiel7.anihyou.R
import com.axiel7.anihyou.data.api.response.DataResult
import com.axiel7.anihyou.ui.screens.main.MainActivity
import com.axiel7.anihyou.ui.theme.AppWidgetColumn
import com.axiel7.anihyou.ui.theme.glanceStringResource
import com.axiel7.anihyou.utils.DateUtils.timestampToDateString
import kotlinx.coroutines.launch
import java.io.File

class AiringWidget : GlanceAppWidget() {

    override val stateDefinition: GlanceStateDefinition<DataResult<List<AiringWidgetQuery.Medium>>>
        get() = object : GlanceStateDefinition<DataResult<List<AiringWidgetQuery.Medium>>> {
            override suspend fun getDataStore(
                context: Context,
                fileKey: String
            ): DataStore<DataResult<List<AiringWidgetQuery.Medium>>> {
                return AiringAnimeDataStore(context)
            }

            override fun getLocation(context: Context, fileKey: String): File {
                return context.dataStoreFile("AiringWidgetDataStore")
            }
        }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val scope = rememberCoroutineScope()
            val result: DataResult<List<AiringWidgetQuery.Medium>> = currentState()
            GlanceTheme {
                if (result is DataResult.Success) {
                    AppWidgetColumn {
                        LazyColumn {
                            items(result.data) { item ->
                                ItemView(item = item)
                            }
                            item {
                                RefreshButton(
                                    onClick = {
                                        scope.launch { update(context, id) }
                                    }
                                )
                            }
                        }
                    }
                } else {
                    val message = (result as? DataResult.Error)?.message
                    AppWidgetColumn(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = message ?: glanceStringResource(R.string.no_information),
                            modifier = GlanceModifier.padding(bottom = 8.dp),
                            style = TextStyle(
                                color = GlanceTheme.colors.onSurface
                            )
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ItemView(item: AiringWidgetQuery.Medium) {
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
                        putExtra("media_id", item.id)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addCategory(item.id.toString())
                    }
                ))
        ) {
            Text(
                text = item.title?.userPreferred.orEmpty(),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface
                ),
                maxLines = 1
            )

            item.nextAiringEpisode?.airingAt?.toLong()?.timestampToDateString(
                format = "EE, d MMM HH:mm"
            )?.let { airingDate ->
                Text(
                    text = airingDate,
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer
                    ),
                    maxLines = 1
                )
            }
        }
    }

    @Composable
    private fun RefreshButton(onClick: () -> Unit) {
        Row(
            modifier = GlanceModifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .clickable(onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                provider = ImageProvider(R.drawable.replay_20),
                contentDescription = glanceStringResource(R.string.refresh),
                modifier = GlanceModifier.padding(end = 8.dp)
            )
            Text(
                text = glanceStringResource(R.string.refresh),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface
                ),
            )
        }
    }
}

class AiringWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AiringWidget()
}