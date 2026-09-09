package com.axiel7.anihyou.widget

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
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
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.itemsIndexed
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.material3.ColorProviders
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.preview.ExperimentalGlancePreviewApi
import androidx.glance.preview.Preview
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
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
import com.materialkolor.ktx.toneColor
import com.materialkolor.palettes.TonalPalette
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.layout.height


class AiringWidget : GlanceAppWidget(), KoinComponent {

    private val networkVariables: NetworkVariables by inject()
    private val defaultPreferencesRepository: DefaultPreferencesRepository by inject()
    private val mediaRepository: MediaRepository by inject()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        networkVariables.accessToken = defaultPreferencesRepository.accessToken.first()

        val widgetColors = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ColorProviders(
                light = dynamicLightColorScheme(context),
                dark = dynamicDarkColorScheme(context)
            )
        } else {
            ColorProviders(
                light = lightColorScheme(),
                dark = darkColorScheme()
            )
        }

        val result = mediaRepository.getAiringWidgetData(page = 1, perPage = 50)
        provideContent {
            val scope = rememberCoroutineScope()
            GlanceTheme(colors = widgetColors) {
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
        val todayString = (System.currentTimeMillis() / 1000).timestampToDateString("yyyy-MM-dd")

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .appWidgetBackground()
                .background(GlanceTheme.colors.widgetBackground)
                .cornerRadius(16.dp)
        ) {

            LazyColumn(modifier = GlanceModifier.fillMaxSize()) {
                item {
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
                        Log.d("WidgetTest", "$currentDay - $isToday ($todayString)")
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = glanceStringResource(R.string.upcoming),
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = GlanceModifier.defaultWeight()
            )

            Box(
                modifier = GlanceModifier
                    .width(52.dp)
                    .height(32.dp)
                    .background(GlanceTheme.colors.primary)
                    .cornerRadius(20.dp)
                    .clickable(onRefresh),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    provider = ImageProvider(R.drawable.replay_20),
                    contentDescription = "Refresh",
                    modifier = GlanceModifier.size(24.dp),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimary)
                )
            }
        }
    }

    @Composable
    private fun ItemView(item: AiringWidgetQuery.Medium, showDate: Boolean, isToday: Boolean) {
        val timestamp = item.nextAiringEpisode?.airingAt?.toLong()
        val dayOfWeek = timestamp?.timestampToDateString("EE")?.lowercase() ?: ""
        val dayOfMonth = timestamp?.timestampToDateString("d") ?: ""

        val baseMediaColor = remember(item.coverImage?.color) {
            item.coverImage?.color?.takeIf { it.isNotBlank() }?.let { hex ->
                runCatching { colorFromHex(hex) }.getOrNull()
            }
        }

        val backgroundModifier = if (baseMediaColor != null) {
            GlanceModifier.background(baseMediaColor.darken(2f))
        } else {
            GlanceModifier.background(GlanceTheme.colors.secondaryContainer)
        }
        val defaultTitleColor = GlanceTheme.colors.onSecondaryContainer
        val defaultSubtitleColor = GlanceTheme.colors.onSecondaryContainer

        val titleTextColor = remember(baseMediaColor, defaultTitleColor) {
            baseMediaColor?.let {
                val tone = TonalPalette.from(it).toneColor(95)
                ColorProvider(day = tone, night = tone)
            } ?: defaultTitleColor
        }

        val subtitleTextColor = remember(baseMediaColor, defaultSubtitleColor) {
            baseMediaColor?.let {
                val tone = TonalPalette.from(it).toneColor(80)
                ColorProvider(day = tone, night = tone)
            } ?: defaultSubtitleColor
        }

        Row(
            modifier = GlanceModifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Column(
                modifier = GlanceModifier
                    .width(48.dp)
                    .padding(top = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (showDate) {
                    Text(
                        text = dayOfWeek,
                        style = TextStyle(
                            color = if (isToday) GlanceTheme.colors.primary else GlanceTheme.colors.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    Spacer(modifier = GlanceModifier.size(4.dp))
                    val boxModifier = if (isToday) {
                        GlanceModifier
                            .size(32.dp)
                            .background(GlanceTheme.colors.primary)
                            .cornerRadius(16.dp)
                    } else {
                        GlanceModifier
                            .size(32.dp)
                            .background(Color.Transparent)
                    }

                    Box(
                        modifier = boxModifier,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayOfMonth,
                            style = TextStyle(
                                color = if (isToday) GlanceTheme.colors.onPrimary else GlanceTheme.colors.onSurface,
                                fontSize = 16.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Column(
                modifier = backgroundModifier
                    .defaultWeight()
                    .cornerRadius(12.dp)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
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
                        color = titleTextColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.size(2.dp))
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
                        color = subtitleTextColor,
                        fontSize = 12.sp
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