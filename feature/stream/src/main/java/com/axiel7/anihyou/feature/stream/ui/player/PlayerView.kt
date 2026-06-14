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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import android.view.LayoutInflater
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import `is`.xyz.mpv.MPVLib
import `is`.xyz.mpv.Utils

import com.axiel7.anihyou.core.common.utils.ContextUtils.openLink
import com.axiel7.anihyou.core.common.utils.ContextUtils.showToast

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

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
    val scope = rememberCoroutineScope()

    val activity = context as? Activity
    var isFullscreen by remember { mutableStateOf(false) }
    var showEpisodeOverlay by remember { mutableStateOf(false) }

    // ── MPV playback state ───────────────────────────────────────────────────
    var playerInstance by remember { mutableStateOf<MPVView?>(null) }
    var isPaused by remember { mutableStateOf(false) }
    var durationMs by remember { mutableStateOf(0L) }
    var isBuffering by remember { mutableStateOf(false) }
    var showControls by remember { mutableStateOf(true) }

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

    // ── MPV event listener ───────────────────────────────────────────────────
    val eventObserver = remember(playerInstance, state.resumePositionMs) {
        object : MPVLib.EventObserver {
            override fun eventProperty(property: String) {}

            override fun eventProperty(property: String, value: Long) {}

            override fun eventProperty(property: String, value: Boolean) {
                activity?.runOnUiThread {
                    if (property == "pause") {
                        isPaused = value
                    }
                    if (property == "paused-for-cache") {
                        isBuffering = value
                    }
                    if (property == "eof-reached" && value) {
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
            }

            override fun eventProperty(property: String, value: String) {}

            override fun eventProperty(property: String, value: Double) {
                activity?.runOnUiThread {
                    if (property == "time-pos") {
                        viewModel.onPositionChanged((value * 1000).toLong())
                    }
                    if (property == "duration") {
                        durationMs = (value * 1000).toLong()
                    }
                }
            }

            override fun eventProperty(property: String, value: `is`.xyz.mpv.MPVNode) {}

            override fun event(eventId: Int, data: `is`.xyz.mpv.MPVNode) {
                activity?.runOnUiThread {
                    if (eventId == MPVLib.MpvEvent.MPV_EVENT_FILE_LOADED) {
                        val resumeMs = state.resumePositionMs
                        if (resumeMs > 0) {
                            MPVLib.setPropertyDouble("time-pos", resumeMs / 1000.0)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(playerInstance) {
        if (playerInstance != null) {
            MPVLib.addObserver(eventObserver)
        }
    }

    DisposableEffect(playerInstance) {
        onDispose {
            playerInstance?.let { view ->
                view.isExiting = true
                val currentPos = MPVLib.getPropertyDouble("time-pos")
                if (currentPos != null && currentPos > 0) {
                    viewModel.saveProgress((currentPos * 1000).toLong())
                }
                MPVLib.removeObserver(eventObserver)
                MPVLib.command("quit")
                try {
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                    // Ignore
                }
                MPVLib.destroy()
            }
        }
    }

    // Save progress periodically
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

    // Once stream URL/referer is ready, play file in MPV
    LaunchedEffect(state.activeStreamUrl, state.activeReferer, playerInstance) {
        val url = state.activeStreamUrl ?: return@LaunchedEffect
        val player = playerInstance ?: return@LaunchedEffect
        
        val referer = state.activeReferer ?: "https://www.miruro.tv/"
        val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        val headersString = "Referer: ${referer.replace(",", "\\,")},User-Agent: ${userAgent.replace(",", "\\,")}"
        
        MPVLib.setPropertyString("user-agent", userAgent)
        MPVLib.setPropertyString("http-header-fields", headersString)
        
        player.playFile(url)
        
        if (state.autoPlay) {
            MPVLib.setPropertyBoolean("pause", false)
        } else {
            MPVLib.setPropertyBoolean("pause", true)
        }
    }

    // Auto-hide controls after 3 seconds of inactivity
    LaunchedEffect(showControls, isPaused) {
        if (showControls && !isPaused) {
            kotlinx.coroutines.delay(3000)
            showControls = false
        }
    }

    BackHandler { onBack() }

    var showSettings by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }

    // Netflix next episode overlay check
    val showNextEpisodeOverlay = remember(state.currentPositionMs, durationMs, state.hasNextEpisode) {
        state.hasNextEpisode && durationMs > 0 && state.currentPositionMs > 0 && (durationMs - state.currentPositionMs) <= 10_000
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
                .clickable { showControls = !showControls }
        ) {
            // ── MPV surface ──────────────────────────────────────────────────
            AndroidView(
                factory = { ctx ->
                    val view = LayoutInflater.from(ctx).inflate(
                        com.axiel7.anihyou.feature.stream.R.layout.mpv_player_view,
                        null
                    ) as MPVView
                    view.apply {
                        Utils.copyAssets(ctx)
                        initialize(ctx.filesDir.path, ctx.cacheDir.path)
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        playerInstance = this
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )

            // ── Loading overlay / Buffering ──────────────────────────────────
            if (state.isLoading || isBuffering) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            // ── Center playback controls ─────────────────────────────────────
            AnimatedVisibility(
                visible = showControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            MPVLib.command("seek", "-10", "relative")
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "Rewind 10s",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            MPVLib.setPropertyBoolean("pause", !isPaused)
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Play" else "Pause",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            MPVLib.command("seek", "10", "relative")
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "Forward 10s",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            // ── Top bar ───────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
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
                        viewModel.skipIntro()?.let { seekTargetMs ->
                            MPVLib.setPropertyDouble("time-pos", seekTargetMs / 1000.0)
                        }
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
                        viewModel.skipOutro()?.let { seekTargetMs ->
                            MPVLib.setPropertyDouble("time-pos", seekTargetMs / 1000.0)
                        }
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

            // ── Bottom Seekbar and prev/next episode controls ───────────────────
            AnimatedVisibility(
                visible = showControls,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Seekbar Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = formatTime(state.currentPositionMs),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Slider(
                            value = state.currentPositionMs.coerceIn(0L, durationMs).toFloat(),
                            onValueChange = { newValue ->
                                viewModel.onPositionChanged(newValue.toLong())
                                playerInstance?.let {
                                    MPVLib.setPropertyDouble("time-pos", newValue / 1000.0)
                                }
                            },
                            valueRange = 0f..(durationMs.coerceAtLeast(1L).toFloat()),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary,
                                inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = formatTime(durationMs),
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    
                    // Prev / Next episode buttons row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
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
        PlayerContent(modifier.fillMaxSize())
    } else {
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
