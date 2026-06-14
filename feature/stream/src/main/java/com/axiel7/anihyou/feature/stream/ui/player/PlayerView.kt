package com.axiel7.anihyou.feature.stream.ui.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import okhttp3.OkHttpClient
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.qualifier.named

import com.axiel7.anihyou.core.common.utils.ContextUtils.openLink
import com.axiel7.anihyou.core.common.utils.ContextUtils.showToast

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun PlayerView(
    animeId: Int,
    provider: String,
    category: String,
    episodeSlug: String,
    episodeNumber: Int,
    totalEpisodes: Int,
    resumePositionMs: Long,
    onBack: () -> Unit,
    onNextEpisode: ((Int) -> Unit)? = null,
    onPreviousEpisode: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val okHttpClient = koinInject<OkHttpClient>(named("plain"))
    val scope = rememberCoroutineScope()

    val activity = context as? Activity
    var isFullscreen by remember { mutableStateOf(false) }
    var showEpisodeOverlay by remember { mutableStateOf(false) }

    // ── Orientation and System Bars ──────────────────────────────────────────
    LaunchedEffect(isFullscreen, activity) {
        val window = activity?.window
        val view = activity?.window?.decorView
        if (window != null && view != null) {
            val controller = androidx.core.view.WindowInsetsControllerCompat(window, view)
            if (isFullscreen) {
                controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            } else {
                controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    DisposableEffect(activity) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            val window = activity?.window
            val view = activity?.window?.decorView
            if (window != null && view != null) {
                val controller = androidx.core.view.WindowInsetsControllerCompat(window, view)
                controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // ── ExoPlayer lifecycle ───────────────────────────────────────────────────
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().also { player ->
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        viewModel.markWatched()
                        if (viewModel.uiState.value.autoNext && viewModel.uiState.value.hasNextEpisode) {
                            val nextEp = viewModel.uiState.value.episodeNumber + 1
                            val nextEpisodeObj = viewModel.uiState.value.episodeList.firstOrNull { it.number == nextEp }
                            if (nextEpisodeObj != null) {
                                val slug = nextEpisodeObj.id.substringAfterLast("/")
                                viewModel.load(
                                    animeId = viewModel.uiState.value.animeId,
                                    provider = viewModel.uiState.value.provider,
                                    category = viewModel.uiState.value.category,
                                    episodeSlug = slug,
                                    episodeNumber = nextEp,
                                    totalEpisodes = viewModel.uiState.value.totalEpisodes,
                                    resumePositionMs = 0L
                                )
                            }
                        }
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.e("PlayerView", "ExoPlayer playback error: ${error.errorCodeName} (${error.errorCode}): ${error.message}", error)
                    if (state.selectedQuality != "auto") {
                        context.showToast("Playback error (${error.errorCodeName}). Retrying with Auto quality...")
                        viewModel.selectQuality("auto")
                    } else {
                        context.showToast("Playback failed: ${error.localizedMessage ?: error.message}. Opening in browser...")
                        viewModel.uiState.value.activeStreamUrl?.let { url ->
                            context.openLink(url)
                        }
                    }
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int,
                ) {
                    val seekTo = viewModel.onPositionChanged(newPosition.positionMs)
                    if (seekTo != null) player.seekTo(seekTo)
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.saveProgress(exoPlayer.currentPosition)
            exoPlayer.release()
        }
    }

    // Save progress every 10 seconds via side-effect on position
    LaunchedEffect(state.currentPositionMs) {
        if (state.currentPositionMs > 0 && state.currentPositionMs % 10_000 < 1_000) {
            viewModel.saveProgress(state.currentPositionMs)
        }
    }

    // Load initial data
    LaunchedEffect(animeId, episodeSlug) {
        viewModel.load(
            animeId = animeId,
            provider = provider,
            category = category,
            episodeSlug = episodeSlug,
            episodeNumber = episodeNumber,
            totalEpisodes = totalEpisodes,
            resumePositionMs = resumePositionMs,
        )
    }

    // Once stream URL/referer is ready, build HLS media source
    LaunchedEffect(state.activeStreamUrl, state.activeReferer) {
        val url = state.activeStreamUrl ?: return@LaunchedEffect
        val currentPos = if (!exoPlayer.currentTimeline.isEmpty) {
            exoPlayer.currentPosition
        } else {
            state.resumePositionMs
        }
        val dataSourceFactory = OkHttpDataSource.Factory(
            okHttpClient.newBuilder().apply {
                addInterceptor { chain ->
                    val referer = state.activeReferer ?: "https://www.miruro.tv/"
                    val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                    chain.proceed(
                        chain.request().newBuilder()
                            .header("Referer", referer)
                            .header("User-Agent", userAgent)
                            .build()
                    )
                }
            }.build()
        )
        val hlsSource = HlsMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(url))
        exoPlayer.setMediaSource(hlsSource)
        exoPlayer.prepare()
        if (currentPos > 0) exoPlayer.seekTo(currentPos)
        if (state.autoPlay) exoPlayer.play()
    }

    // Handle quality selection via TrackSelectionParameters
    LaunchedEffect(state.selectedQuality) {
        val quality = state.selectedQuality
        val maxVideoSize = when (quality.removeSuffix("p").toIntOrNull()) {
            1080 -> 1920 to 1080
            720 -> 1280 to 720
            480 -> 854 to 480
            360 -> 640 to 360
            else -> null
        }
        if (maxVideoSize != null) {
            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                .setMaxVideoSize(maxVideoSize.first, maxVideoSize.second)
                .setForceHighestSupportedBitrate(true)
                .build()
        } else {
            exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters.buildUpon()
                .clearVideoSizeConstraints()
                .build()
        }
    }

    BackHandler { onBack() }

    var showSettings by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }

    // Netflix next episode overlay check
    val showNextEpisodeOverlay = remember(state.currentPositionMs, exoPlayer.duration, state.hasNextEpisode) {
        val duration = exoPlayer.duration
        val pos = state.currentPositionMs
        state.hasNextEpisode && duration > 0 && pos > 0 && (duration - pos) <= 10_000
    }

    @Composable
    fun PlayerContent(playerModifier: Modifier = Modifier) {
        Box(
            modifier = playerModifier
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        if (dragAmount < -15) {
                            showEpisodeOverlay = true
                        }
                    }
                }
        ) {
            // ── ExoPlayer surface ─────────────────────────────────────────────
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = true
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        controllerHideOnTouch = true
                        controllerAutoShow = true
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )

            // ── Loading overlay ───────────────────────────────────────────────
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
                Text(
                    text = "Episode ${state.episodeNumber}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )
                
                // Fullscreen toggle button
                IconButton(onClick = { isFullscreen = !isFullscreen }) {
                    Icon(
                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                        contentDescription = "Toggle Fullscreen",
                        tint = Color.White
                    )
                }

                // Episodes Overlay Trigger
                IconButton(onClick = { showEpisodeOverlay = !showEpisodeOverlay }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Episodes",
                        tint = Color.White
                    )
                }

                // Quality selector
                Box {
                    IconButton(onClick = { showQualityMenu = true }) {
                        Text(
                            text = state.selectedQuality,
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    DropdownMenu(
                        expanded = showQualityMenu,
                        onDismissRequest = { showQualityMenu = false },
                    ) {
                        state.availableQualities.forEach { quality ->
                            DropdownMenuItem(
                                text = { Text(quality) },
                                onClick = {
                                    viewModel.selectQuality(quality)
                                    showQualityMenu = false
                                },
                                leadingIcon = if (quality == state.selectedQuality) ({
                                    Icon(Icons.Default.Check, contentDescription = null)
                                }) else null,
                            )
                        }
                    }
                }
                // Settings
                IconButton(onClick = { showSettings = !showSettings }) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            // ── Skip Intro button ─────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.showSkipIntro,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp),
            ) {
                Button(
                    onClick = {
                        viewModel.skipIntro()?.let { exoPlayer.seekTo(it) }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.9f)),
                ) {
                    Text("Skip Intro", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            // ── Skip Outro button ─────────────────────────────────────────────
            AnimatedVisibility(
                visible = state.showSkipOutro,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp),
            ) {
                Button(
                    onClick = {
                        viewModel.skipOutro()?.let { exoPlayer.seekTo(it) }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.9f)),
                ) {
                    Text("Skip Outro", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }

            // ── Netflix Next Episode Button ───────────────────────────────────
            AnimatedVisibility(
                visible = showNextEpisodeOverlay,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 80.dp),
            ) {
                Button(
                    onClick = {
                        if (state.hasNextEpisode) {
                            viewModel.markWatched()
                            val nextEp = state.episodeNumber + 1
                            val nextEpisodeObj = state.episodeList.firstOrNull { it.number == nextEp }
                            if (nextEpisodeObj != null) {
                                val slug = nextEpisodeObj.id.substringAfterLast("/")
                                viewModel.load(
                                    animeId = state.animeId,
                                    provider = state.provider,
                                    category = state.category,
                                    episodeSlug = slug,
                                    episodeNumber = nextEp,
                                    totalEpisodes = state.totalEpisodes,
                                    resumePositionMs = 0L
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                ) {
                    Text("Next Episode (${state.episodeNumber + 1})")
                }
            }

            // ── Prev / Next episode controls ──────────────────────────────────
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                if (state.hasPreviousEpisode) {
                    IconButton(
                        onClick = {
                            val prevEp = state.episodeNumber - 1
                            val prevEpisodeObj = state.episodeList.firstOrNull { it.number == prevEp }
                            if (prevEpisodeObj != null) {
                                val slug = prevEpisodeObj.id.substringAfterLast("/")
                                viewModel.load(
                                    animeId = state.animeId,
                                    provider = state.provider,
                                    category = state.category,
                                    episodeSlug = slug,
                                    episodeNumber = prevEp,
                                    totalEpisodes = state.totalEpisodes,
                                    resumePositionMs = 0L
                                )
                            } else {
                                onPreviousEpisode?.invoke(prevEp)
                            }
                        },
                    ) {
                        Icon(Icons.Default.SkipPrevious, contentDescription = "Previous episode", tint = Color.White)
                    }
                }
                if (state.hasNextEpisode) {
                    IconButton(
                        onClick = {
                            viewModel.markWatched()
                            val nextEp = state.episodeNumber + 1
                            val nextEpisodeObj = state.episodeList.firstOrNull { it.number == nextEp }
                            if (nextEpisodeObj != null) {
                                val slug = nextEpisodeObj.id.substringAfterLast("/")
                                viewModel.load(
                                    animeId = state.animeId,
                                    provider = state.provider,
                                    category = state.category,
                                    episodeSlug = slug,
                                    episodeNumber = nextEp,
                                    totalEpisodes = state.totalEpisodes,
                                    resumePositionMs = 0L
                                )
                            } else {
                                onNextEpisode?.invoke(nextEp)
                            }
                        },
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next episode", tint = Color.White)
                    }
                }
            }

            // ── Settings panel ────────────────────────────────────────────────
            AnimatedVisibility(
                visible = showSettings,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 56.dp, end = 8.dp),
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                        .width(240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text("Playback Settings", color = Color.White, fontWeight = FontWeight.Bold)
                    SettingsToggle("Auto-play", state.autoPlay) { viewModel.toggleAutoPlay() }
                    SettingsToggle("Auto-next episode", state.autoNext) { viewModel.toggleAutoNext() }
                    SettingsToggle("Auto-skip intro", state.autoSkipIntro) { viewModel.toggleAutoSkipIntro() }
                    SettingsToggle("Auto-skip outro", state.autoSkipOutro) { viewModel.toggleAutoSkipOutro() }
                }
            }

            // ── Episode selector overlay (swipe up / menu activated) ──────────
            AnimatedVisibility(
                visible = showEpisodeOverlay,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Black.copy(alpha = 0.9f))
                        .clickable(enabled = false) {}
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Episodes List",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(onClick = { showEpisodeOverlay = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White
                                )
                            }
                        }
                        
                        LazyRow(
                            contentPadding = PaddingValues(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(state.episodeList) { ep ->
                                val isCurrent = ep.number == state.episodeNumber
                                Box(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .fillMaxHeight()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                            else Color.DarkGray.copy(alpha = 0.5f)
                                        )
                                        .border(
                                            width = if (isCurrent) 2.dp else 1.dp,
                                            color = if (isCurrent) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            val slug = ep.id.substringAfterLast("/")
                                            viewModel.load(
                                                animeId = state.animeId,
                                                provider = state.provider,
                                                category = state.category,
                                                episodeSlug = slug,
                                                episodeNumber = ep.number,
                                                totalEpisodes = state.totalEpisodes,
                                                resumePositionMs = 0L
                                            )
                                            showEpisodeOverlay = false
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Episode ${ep.number}",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (ep.title != null) {
                                            Text(
                                                text = ep.title,
                                                color = Color.LightGray,
                                                style = MaterialTheme.typography.labelSmall,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (isFullscreen) {
        // Landscape Fullscreen mode
        PlayerContent(modifier.fillMaxSize())
    } else {
        // Portrait mode: top 16:9 player surface, bottom scrollable episode list
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            PlayerContent(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Playing Episode ${state.episodeNumber}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${state.provider.uppercase()} · ${state.category.uppercase()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
                
                item {
                    Text(
                        text = "Episodes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                items(state.episodeList) { ep ->
                    val isCurrent = ep.number == state.episodeNumber
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val slug = ep.id.substringAfterLast("/")
                                viewModel.load(
                                    animeId = state.animeId,
                                    provider = state.provider,
                                    category = state.category,
                                    episodeSlug = slug,
                                    episodeNumber = ep.number,
                                    totalEpisodes = state.totalEpisodes,
                                    resumePositionMs = 0L
                                )
                            }
                            .background(
                                if (isCurrent) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Episode ${ep.number}",
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.weight(1f)
                        )
                        if (ep.title != null) {
                            Text(
                                text = ep.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .weight(1.5f)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsToggle(label: String, checked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = Color.White, style = MaterialTheme.typography.bodySmall)
        Switch(checked = checked, onCheckedChange = { onToggle() })
    }
}
