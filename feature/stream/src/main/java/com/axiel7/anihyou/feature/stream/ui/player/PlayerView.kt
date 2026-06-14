package com.axiel7.anihyou.feature.stream.ui.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
    DisposableEffect(activity) {
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = originalOrientation
        }
    }

    // ── ExoPlayer lifecycle ───────────────────────────────────────────────────
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().also { player ->
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        viewModel.markWatched()
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    context.showToast("Playback failed. Opening in browser...")
                    viewModel.uiState.value.activeStreamUrl?.let { url ->
                        context.openLink(url)
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
        // Periodic position save via tick listener alternative: use LaunchedEffect below
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

    // Once stream URL is ready, build HLS media source
    LaunchedEffect(state.activeStreamUrl) {
        val url = state.activeStreamUrl ?: return@LaunchedEffect
        val currentPos = if (!exoPlayer.currentTimeline.isEmpty) {
            exoPlayer.currentPosition
        } else {
            state.resumePositionMs
        }
        val dataSourceFactory = OkHttpDataSource.Factory(
            okHttpClient.newBuilder().apply {
                state.activeReferer?.let { referer ->
                    addInterceptor { chain ->
                        chain.proceed(
                            chain.request().newBuilder()
                                .addHeader("Referer", referer)
                                .build()
                        )
                    }
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

    // Handle quality change without re-loading everything
    LaunchedEffect(state.activeStreamUrl, state.selectedQuality) {
        // activeStreamUrl is already updated by selectQuality(); ExoPlayer needs to reload
        // This effect runs when the url changes — handled above already
    }

    BackHandler { onBack() }

    var showSettings by remember { mutableStateOf(false) }
    var showQualityMenu by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // ── ExoPlayer surface ─────────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                    // Hide default controller so we draw our own overlays
                    controllerHideOnTouch = true
                    controllerAutoShow = true
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // ── Loading overlay ───────────────────────────────────────────────────
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        // ── Top bar ───────────────────────────────────────────────────────────
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

        // ── Skip Intro button ─────────────────────────────────────────────────
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

        // ── Skip Outro button ─────────────────────────────────────────────────
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

        // ── Prev / Next episode controls ──────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            if (state.hasPreviousEpisode) {
                IconButton(
                    onClick = { onPreviousEpisode?.invoke(state.episodeNumber - 1) },
                ) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous episode", tint = Color.White)
                }
            }
            if (state.hasNextEpisode) {
                IconButton(
                    onClick = {
                        viewModel.markWatched()
                        onNextEpisode?.invoke(state.episodeNumber + 1)
                    },
                ) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next episode", tint = Color.White)
                }
            }
        }

        // ── Settings panel ────────────────────────────────────────────────────
        AnimatedVisibility(
            visible = showSettings,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 56.dp, end = 8.dp),
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
