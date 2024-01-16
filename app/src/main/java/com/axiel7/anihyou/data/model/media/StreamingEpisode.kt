package com.axiel7.anihyou.data.model.media

import com.axiel7.anihyou.MediaDetailsQuery

private val episodeNumberRegex = Regex("\\d+")

fun MediaDetailsQuery.StreamingEpisode.episodeNumber() =
    title?.let { episodeNumberRegex.find(it)?.value?.toIntOrNull() }