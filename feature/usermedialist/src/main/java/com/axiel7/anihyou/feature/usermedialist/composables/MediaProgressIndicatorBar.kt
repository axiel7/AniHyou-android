package com.axiel7.anihyou.feature.usermedialist.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.axiel7.anihyou.core.network.type.MediaFormat
import kotlin.math.exp

@Composable
fun MediaProgressIndicatorBar(
    progress: Int,
    total: Int?,
    released: Int?,
    format: MediaFormat?,
    modifier: Modifier = Modifier,
    isVolumeTracking: Boolean = false,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    trackColor: Color = MaterialTheme.colorScheme.secondaryContainer,
) {
    val fraction = remember(progress, total, released, format, isVolumeTracking) {
        if (progress == 0) return@remember 0f

        if (total != null && total > 0) {
            val standardFraction = (progress.toFloat() / total).coerceIn(0f, 1f)
            return@remember standardFraction
        }

        val safeReleased = maxOf(released ?: 1, progress, 1)

        val k = when (format) {
            MediaFormat.OVA, MediaFormat.SPECIAL -> 0.35f
            MediaFormat.TV_SHORT -> 0.15f
            MediaFormat.TV -> 0.07f
            MediaFormat.MANGA, MediaFormat.NOVEL -> {
                if (isVolumeTracking) 0.06f else 0.015f
            }

            else -> 0.07f
        }

        val maxCap =
            if (format == MediaFormat.OVA || format == MediaFormat.SPECIAL) 0.80f else 0.88f
        val horizon = maxCap * (1f - exp(-k * safeReleased.toFloat()))

        val fillRatio = (progress.toFloat() / safeReleased).coerceIn(0f, 1f)

        (fillRatio * horizon)
    }

    Canvas(modifier = modifier.height(4.dp)) {
        val w = size.width
        val stroke = size.height
        val y = size.height / 2f
        val radius = stroke / 2f

        val safeReleased = released ?: total ?: progress
        val gap = 3.dp.toPx()

        if (total != null && total > 0) {
            val completedRatio = (progress.toFloat() / total).coerceIn(0f, 1f)
            val airedRatio = (safeReleased.toFloat() / total).coerceIn(0f, 1f)

            if (completedRatio == 0f) {
                drawLine(
                    color = trackColor,
                    start = Offset(radius, y),
                    end = Offset(w - radius, y),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
                if (airedRatio > 0f) {
                    val airedDrawEnd = maxOf(radius, (w * airedRatio) - radius)
                    drawLine(
                        color = trackColor,
                        start = Offset(radius, y),
                        end = Offset(airedDrawEnd, y),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                }
            } else if (completedRatio == 1f) {
                drawLine(
                    color = indicatorColor,
                    start = Offset(radius, y),
                    end = Offset(w - radius, y),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )
            } else {
                var completedPillWidth = (w - gap) * completedRatio
                var trackPillWidth = (w - gap) * (1 - completedRatio)

                if (completedPillWidth < stroke) {
                    completedPillWidth = stroke
                    trackPillWidth = w - gap - stroke
                } else if (trackPillWidth < stroke) {
                    trackPillWidth = stroke
                    completedPillWidth = w - gap - stroke
                }

                val completedDrawEnd = completedPillWidth - radius
                drawLine(
                    color = indicatorColor,
                    start = Offset(radius, y),
                    end = Offset(completedDrawEnd, y),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )

                val trackVisualStart = completedPillWidth + gap
                val trackDrawStart = trackVisualStart + radius
                val trackDrawEnd = w - radius
                drawLine(
                    color = trackColor,
                    start = Offset(trackDrawStart, y),
                    end = Offset(trackDrawEnd, y),
                    strokeWidth = stroke,
                    cap = StrokeCap.Round
                )

                if (airedRatio > completedRatio) {
                    val airedTrackFraction = (airedRatio - completedRatio) / (1f - completedRatio)
                    val airedVisualWidth = trackPillWidth * airedTrackFraction
                    val airedDrawEnd =
                        maxOf(trackDrawStart, trackVisualStart + airedVisualWidth - radius)

                    drawLine(
                        color = trackColor,
                        start = Offset(trackDrawStart, y),
                        end = Offset(airedDrawEnd, y),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                }
            }
        } else {
            drawLine(
                color = trackColor,
                start = Offset(radius, y),
                end = Offset(w - radius, y),
                strokeWidth = stroke,
                cap = StrokeCap.Round
            )

            if (progress > 0) {
                val completedRatio = (progress.toFloat() / safeReleased).coerceIn(0f, 1f)
                val completedVisualWidth = w * fraction
                val completedDrawEnd = maxOf(radius, completedVisualWidth - radius)

                if (progress < safeReleased) {
                    drawLine(
                        color = indicatorColor,
                        start = Offset(radius, y),
                        end = Offset(completedDrawEnd, y),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )

                    val trackVisualStart = completedVisualWidth + gap
                    val trackDrawStart = trackVisualStart + radius

                    val horizonWidth = w * (fraction / completedRatio)
                    val airedDrawEnd = maxOf(trackDrawStart, horizonWidth - radius)

                    drawLine(
                        color = trackColor,
                        start = Offset(trackDrawStart, y),
                        end = Offset(airedDrawEnd, y),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                } else {
                    val brush = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.0f to indicatorColor,
                            0.7f to indicatorColor,
                            1.0f to Color.Transparent
                        ),
                        startX = 0f,
                        endX = completedVisualWidth
                    )
                    drawLine(
                        brush = brush,
                        start = Offset(radius, y),
                        end = Offset(completedDrawEnd, y),
                        strokeWidth = stroke,
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}