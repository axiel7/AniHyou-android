package com.axiel7.anihyou.core.model.media

import com.axiel7.anihyou.core.network.MediaDetailsQuery

private val episodeNumberRegex = Regex("\\d+")

fun MediaDetailsQuery.StreamingEpisode.episodeNumber() =
    title?.let { episodeNumberRegex.find(it)?.value?.toIntOrNull() }