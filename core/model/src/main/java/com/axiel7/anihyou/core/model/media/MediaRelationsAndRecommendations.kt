package com.axiel7.anihyou.core.model.media

import com.axiel7.anihyou.core.network.MediaRelationsAndRecommendationsQuery

data class MediaRelationsAndRecommendations(
    val relations: List<MediaRelationsAndRecommendationsQuery.Edge>,
    val recommendations: List<MediaRelationsAndRecommendationsQuery.Node>,
)
