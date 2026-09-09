package com.axiel7.anihyou.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
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
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.color.DynamicThemeColorProviders
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import com.axiel7.anihyou.core.base.APP_PACKAGE_NAME
import com.axiel7.anihyou.core.base.DataResult
import com.axiel7.anihyou.core.base.UNKNOWN_CHAR
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampToDateString
import com.axiel7.anihyou.core.common.utils.DateUtils.timestampToTimeString
import com.axiel7.anihyou.core.domain.repository.DefaultPreferencesRepository
import com.axiel7.anihyou.core.domain.repository.MediaRepository
import com.axiel7.anihyou.core.model.DeepLink
import com.axiel7.anihyou.core.model.media.exampleAiringWidgetEntry
import com.axiel7.anihyou.core.network.AiringWidgetQuery
import com.axiel7.anihyou.core.network.NetworkVariables
import com.axiel7.anihyou.core.resources.ColorUtils.colorFromHex
import com.axiel7.anihyou.core.resources.R
import com.materialkolor.ktx.darken
import com.materialkolor.ktx.from
import com.materialkolor.ktx.harmonize
import com.materialkolor.ktx.toneColor
import com.materialkolor.palettes.TonalPalette
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
            GlanceTheme(colors = DynamicThemeColorProviders) {
                Content(
                    result = result,
                    onRefresh = { scope.launch { update(context, id) } },
                )
            }
        }
    }

    @Composable
    private fun Content(
        result: DataResult<List<AiringWidgetQuery.Medium>>,
        onRefresh: () -> Unit,
    ) {
        val todayString = (System.currentTimeMillis() / 1000).timestampToDateString("yyyy-MM-dd")

        Scaffold(
            horizontalPadding = 0.dp
        ) {
            LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                item(itemId = 0) {
                    Header(onRefresh = onRefresh)
                }
                if (result is DataResult.Success) {
                    itemsIndexed(
                        items = result.data,
                        itemId = { _, item -> item.id.toLong() }
                    ) { index, item ->
                        val currentDay = item.nextAiringEpisode?.airingAt?.toLong()
                            ?.timestampToDateString("yyyy-MM-dd")
                        val previousDay = if (index > 0) {
                            result.data[index - 1].nextAiringEpisode?.airingAt?.toLong()
                                ?.timestampToDateString("yyyy-MM-dd")
                        } else null

                        val showDate = currentDay != previousDay
                        val isToday = currentDay == todayString
                        ItemView(item = item, showDate = showDate, isToday = isToday)
                    }
                } else {
                    item {
                        Column(
                            modifier = GlanceModifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (result is DataResult.Loading) {
                                CircularProgressIndicator(color = GlanceTheme.colors.primary)
                            } else if (result is DataResult.Error) {
                                Text(
                                    text = result.message,
                                    modifier = GlanceModifier.padding(bottom = 8.dp),
                                    style = TextStyle(color = GlanceTheme.colors.onSurface)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Header(onRefresh: () -> Unit) {
        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = GlanceModifier.width(20.dp))
            Text(
                text = glanceStringResource(R.string.upcoming),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                ),
                maxLines = 1,
                modifier = GlanceModifier.defaultWeight()
            )

            Box(
                modifier = GlanceModifier
                    .width(54.dp)
                    .height(32.dp)
                    .background(GlanceTheme.colors.primary)
                    .cornerRadius(20.dp)
                    .clickable(onRefresh),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.replay_20),
                    contentDescription = glanceStringResource(R.string.refresh),
                    modifier = GlanceModifier.size(20.dp),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimary)
                )
            }
            Spacer(modifier = GlanceModifier.width(12.dp))
        }
    }

    @Composable
    private fun ItemView(item: AiringWidgetQuery.Medium, showDate: Boolean, isToday: Boolean) {
        val timestamp = item.nextAiringEpisode?.airingAt?.toLong()
        val dayOfWeek = timestamp?.timestampToDateString("E").orEmpty()
        val dayOfMonth = timestamp?.timestampToDateString("d").orEmpty()

        val baseMediaColor = remember(item.coverImage?.color) {
            item.coverImage?.color?.takeIf { it.isNotBlank() }?.let { hex ->
                runCatching { colorFromHex(hex) }.getOrNull()
            }
        }

        val primaryColor = GlanceTheme.colors.primary.getColor(LocalContext.current)

        val backgroundModifier = if (baseMediaColor != null) {
            GlanceModifier.background(baseMediaColor.harmonize(primaryColor).darken(2f))
        } else {
            GlanceModifier.background(GlanceTheme.colors.secondaryContainer)
        }
        val defaultTextColor = GlanceTheme.colors.onSecondaryContainer

        val textColor = remember(baseMediaColor, defaultTextColor) {
            baseMediaColor?.let {
                val tone = TonalPalette.from(it).toneColor(95)
                ColorProvider(day = tone, night = tone)
            } ?: defaultTextColor
        }

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .padding(end = 12.dp, start = 2.dp)
        ) {
            Column(
                modifier = GlanceModifier
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showDate) {
                    val dateModifier = if (isToday) {
                        GlanceModifier
                            .size(38.dp)
                            .background(GlanceTheme.colors.primary)
                            .cornerRadius(38.dp)
                    } else {
                        GlanceModifier
                            .size(38.dp)
                            .background(Color.Transparent)
                    }

                    Column(
                        modifier = dateModifier,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (isToday) {
                            Text(
                                text = "$dayOfWeek\n$dayOfMonth",
                                style = TextStyle(
                                    color = GlanceTheme.colors.onPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                ),
                                maxLines = 2,
                            )
                        } else {
                            Text(
                                text = dayOfWeek,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                ),
                                maxLines = 1,
                            )
                            Text(
                                text = dayOfMonth,
                                style = TextStyle(
                                    color = GlanceTheme.colors.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Normal,
                                    textAlign = TextAlign.Center,
                                )
                            )
                        }
                    }
                } else {
                    Spacer(modifier = GlanceModifier.width(36.dp))
                }
            }

            Column(
                modifier = backgroundModifier
                    .defaultWeight()
                    .cornerRadius(12.dp)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clickable(
                        actionStartActivity(
                            LocalContext.current.packageManager
                                .getLaunchIntentForPackage(APP_PACKAGE_NAME)
                                ?.apply {
                                    action = DeepLink.Type.ANIME.intentAction
                                    putExtra("content_id", item.id)
                                    putExtra("widget", true)
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
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Text(
                    text = item.nextAiringEpisode?.let { nextAiringEpisode ->
                        glanceStringResource(
                            R.string.episode_airing_at,
                            nextAiringEpisode.episode,
                            nextAiringEpisode.airingAt.toLong().timestampToTimeString()
                                ?: UNKNOWN_CHAR
                        )
                    } ?: glanceStringResource(R.string.unknown),
                    style = TextStyle(
                        color = textColor,
                        fontSize = 13.sp
                    ),
                    maxLines = 1
                )
            }

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