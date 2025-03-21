package com.axiel7.anihyou.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.CircularProgressIndicator
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.axiel7.anihyou.core.common.APP_PACKAGE_NAME
import com.axiel7.anihyou.core.common.DataResult
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.model.media.exampleAiringWidgetEntry
import com.axiel7.anihyou.core.network.AiringWidgetQuery
import com.axiel7.anihyou.core.network.NetworkVariables
import com.axiel7.anihyou.core.resources.R
import com.axiel7.anihyou.core.ui.utils.DateUtils.timestampToDateString
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AiringWidget : GlanceAppWidget(), KoinComponent {

    private val networkVariables: NetworkVariables by inject()
    private val defaultPreferencesRepository: DefaultPreferencesRepository by inject()
    private val mediaRepository: MediaRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        networkVariables.accessToken = defaultPreferencesRepository.accessToken.first()

        val result = mediaRepository.getAiringWidgetData(page = 1, perPage = 50)

        provideContent {
            val scope = rememberCoroutineScope()
            GlanceTheme {
                Content(
                    result = result,
                    onRefresh = {
                        scope.launch { update(context, id) }
                    }
                )
            }
        }
    }

    @Composable
    private fun Content(
        result: DataResult<List<AiringWidgetQuery.Medium>>,
        onRefresh: () -> Unit,
    ) {
        if (result is DataResult.Success) {
            AppWidgetColumn {
                LazyColumn {
                    items(result.data) { item ->
                        ItemView(item = item)
                    }
                    item {
                        RefreshButton(onClick = onRefresh)
                    }
                }
            }
        } else {
            AppWidgetColumn(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (result is DataResult.Loading) {
                    CircularProgressIndicator(
                        color = GlanceTheme.colors.primary
                    )
                } else if (result is DataResult.Error) {
                    val message = result.message
                    Text(
                        text = message,
                        modifier = GlanceModifier.padding(bottom = 8.dp),
                        style = TextStyle(
                            color = GlanceTheme.colors.onSurface
                        )
                    )
                }
                RefreshButton(onClick = onRefresh)
            }
        }
    }

    @Composable
    private fun ItemView(item: AiringWidgetQuery.Medium) {
        Column(
            modifier = GlanceModifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .clickable(
                    actionStartActivity(
                        LocalContext.current.packageManager
                            .getLaunchIntentForPackage(APP_PACKAGE_NAME)
                            ?.apply {
                                action = "media_details"
                                putExtra("media_id", item.id)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                addCategory(item.id.toString())
                            } ?: Intent()
                    )
                )
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

    @OptIn(ExperimentalGlancePreviewApi::class)
    @Preview(widthDp = 255, heightDp = 150)
    @Composable
    private fun Preview() {
        GlanceTheme {
            Content(
                result = DataResult.Success(
                    data = listOf(
                        exampleAiringWidgetEntry,
                        exampleAiringWidgetEntry,
                        exampleAiringWidgetEntry,
                        exampleAiringWidgetEntry,
                    )
                ),
                onRefresh = {}
            )
        }
    }
}

class AiringWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = AiringWidget()
}